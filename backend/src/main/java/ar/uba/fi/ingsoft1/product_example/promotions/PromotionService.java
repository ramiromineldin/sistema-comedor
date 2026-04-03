package ar.uba.fi.ingsoft1.product_example.promotions;

import ar.uba.fi.ingsoft1.product_example.products.Product;
import ar.uba.fi.ingsoft1.product_example.products.ProductService;
import ar.uba.fi.ingsoft1.product_example.promotions.dto.PromotionDTO;
import ar.uba.fi.ingsoft1.product_example.promotions.dto.PromotionCreateDTO;
import ar.uba.fi.ingsoft1.product_example.promotions.dto.PromotionItemCreateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final ProductService productService;

    public List<PromotionDTO> getAllPromotions() {
        List<Promotion> promotions = promotionRepository.findAll();
        return promotions.stream().map(PromotionDTO::fromPromotion).collect(Collectors.toList());
    }

    public List<PromotionDTO> getActivePromotionsForStudents() {
        var now = LocalDateTime.now();
        
        List<Promotion> promotions = promotionRepository.findActiveNow(now);
        
        // No filtramos, solo convertimos a DTO
        // El campo 'available' en el DTO indicará si está disponible o no
        return promotions.stream()
                .map(PromotionDTO::fromPromotion)
                .collect(Collectors.toList());
    }

    public PromotionDTO createPromotion(PromotionCreateDTO request) {
        validateBasicRequest(request);

        Promotion promotion = new Promotion();
        promotion.setName(request.name());
        promotion.setDescription(request.description());
        promotion.setStartDate(request.startDate());
        promotion.setEndDate(request.endDate());
        promotion.setType(request.type());
        promotion.setActive(true);

        promotion.validateWithStrategy(request);
        promotion.configureWithStrategy(request);

        for (var itemReq : request.items()) {
            Product product = productService.findById(itemReq.productId());
            PromotionItem item = new PromotionItem();
            item.setProduct(product);
            item.setOriginalPrice(product.getPrice());
            promotion.addItem(item);
        }

        // Calcular precios usando la estrategia
        promotion.calculatePrices();

        Promotion saved = promotionRepository.save(promotion);
        return PromotionDTO.fromPromotion(saved);
    }

    public Promotion findPromotion(Long id) {
        if (id == null) throw new IllegalArgumentException("El id de la promoción es obligatorio");
        return promotionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Promoción no encontrada con id: " + id));
    }

    public PromotionDTO updatePromotion(Long id, PromotionCreateDTO request) {
        var promotion = findPromotion(id);
        validateBasicRequest(request);

        promotion.setName(request.name());
        promotion.setDescription(request.description());
        promotion.setStartDate(request.startDate());
        promotion.setEndDate(request.endDate());
        promotion.setType(request.type());

        // Limpiar campos anteriores
        promotion.setDiscountPercentage(null);
        promotion.setDiscountAmount(null);
        promotion.setMinimumPurchaseAmount(null);
        promotion.setBuyQuantity(null);
        promotion.setPayQuantity(null);
        promotion.setRecurringDay(null);

        // Validar y configurar usando la estrategia del tipo de promoción
        promotion.validateWithStrategy(request);
        promotion.configureWithStrategy(request);

        promotion.getItems().clear();
        for (var itemReq : request.items()) {
            Product product = productService.findById(itemReq.productId());
            PromotionItem item = new PromotionItem();
            item.setProduct(product);
            item.setOriginalPrice(product.getPrice());
            promotion.addItem(item);
        }

        // Calcular precios usando la estrategia
        promotion.calculatePrices();

        Promotion saved = promotionRepository.save(promotion);
        return PromotionDTO.fromPromotion(saved);
    }

    public PromotionDTO desactivatePromotion(Long id) {
        var promotion = findPromotion(id);
        promotion.setActive(false);
        promotionRepository.save(promotion);
        return PromotionDTO.fromPromotion(promotion);
    }

    public void deletePromotion(Long id) {
        var promotion = findPromotion(id);
        promotionRepository.delete(promotion);
    }

    public PromotionDTO activatePromotion(Long id) {
        var promotion = findPromotion(id);
        promotion.setActive(true);
        promotionRepository.save(promotion);
        return PromotionDTO.fromPromotion(promotion);
    }

    public PromotionDTO addItemToPromotion(Long id, PromotionItemCreateDTO itemRequest) {
        var promotion = findPromotion(id);
        Product product = productService.findById(itemRequest.productId());
        PromotionItem item = new PromotionItem();
        item.setProduct(product);
        item.setOriginalPrice(product.getPrice());
        promotion.addItem(item);

        // Recalcular precios
        promotion.calculatePrices();

        Promotion saved = promotionRepository.save(promotion);
        return PromotionDTO.fromPromotion(saved);
    }

    public PromotionDTO removeItemFromPromotion(Long id, Long itemId) {
        var promotion = findPromotion(id);
        PromotionItem itemToRemove = promotion.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item no encontrado en la promoción con id: " + itemId));
        promotion.removeItem(itemToRemove);

        // Recalcular precios
        promotion.calculatePrices();

        Promotion saved = promotionRepository.save(promotion);
        return PromotionDTO.fromPromotion(saved);
    }

    private void validateBasicRequest(PromotionCreateDTO request) {
        if (request == null) throw new IllegalArgumentException("Request es obligatorio");
        if (request.name() == null || request.name().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la promoción es obligatorio");
        }
        
        // Para promociones recurrentes, las fechas se setean automáticamente
        if (request.type() != PromotionType.RECURRING_DAY_DISCOUNT) {
            if (request.startDate() == null || request.endDate() == null) {
                throw new IllegalArgumentException("Fechas de vigencia obligatorias");
            }
            if (request.endDate().isBefore(request.startDate())) {
                throw new IllegalArgumentException("La fecha de fin debe ser posterior a la de inicio");
            }
        }
        
        if (request.type() == null) {
            throw new IllegalArgumentException("El tipo de promoción es obligatorio");
        }
        if (request.type() != PromotionType.AMOUNT_DISCOUNT &&
                (request.items() == null || request.items().isEmpty())) {
            throw new IllegalArgumentException("Debe incluir al menos un producto en la promoción");
        }
    }

    public List<Promotion> findActiveNowEntities(LocalDateTime now) {
        return promotionRepository.findActiveNow(now);
    }

}
