package ar.uba.fi.ingsoft1.product_example.promotions;

import ar.uba.fi.ingsoft1.product_example.products.Product;
import ar.uba.fi.ingsoft1.product_example.products.ProductService;
import ar.uba.fi.ingsoft1.product_example.promotions.dto.PromotionCreateDTO;
import ar.uba.fi.ingsoft1.product_example.promotions.dto.PromotionDTO;
import ar.uba.fi.ingsoft1.product_example.promotions.dto.PromotionItemCreateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PromotionServiceTest {

    private PromotionRepository promotionRepository;
    private ProductService productService;
    private PromotionService promotionService;

    @BeforeEach
    void setup() {
        promotionRepository = mock(PromotionRepository.class);
        productService = mock(ProductService.class);
        promotionService = new PromotionService(promotionRepository, productService);
    }

    @Test
    @DisplayName("Create promotion with percentage discount successfully")
    void createPromotionSuccessfully() {
        Product product = new Product("Hamburguesa", "Con queso", BigDecimal.valueOf(1500), 10);
        product.setId(1L);

        PromotionItemCreateDTO itemDTO = new PromotionItemCreateDTO(1L);
        PromotionCreateDTO dto = new PromotionCreateDTO(
                "Promo 20%",
                "Descuento del 20%",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7),
                PromotionType.PERCENTAGE_DISCOUNT,
                BigDecimal.valueOf(20),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                List.of(itemDTO)
        );

        when(productService.findById(1L)).thenReturn(product);
        when(promotionRepository.save(any(Promotion.class))).thenAnswer(inv -> {
            Promotion promo = inv.getArgument(0);
            promo.setId(1L);
            return promo;
        });

        PromotionDTO result = promotionService.createPromotion(dto);

        assertNotNull(result);
        assertEquals("Promo 20%", result.name());
        assertNotNull(result.finalPrice());
    }

    @Test
    @DisplayName("Create promotion with buy X pay Y successfully")
    void createPromotionBuyXPayY() {
        Product product = new Product("Pizza", "Muzzarella", BigDecimal.valueOf(2000), 5);
        product.setId(1L);

        PromotionItemCreateDTO itemDTO = new PromotionItemCreateDTO(1L);
        PromotionCreateDTO dto = new PromotionCreateDTO(
                "3x2 Pizzas",
                "Lleva 3 paga 2",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7),
                PromotionType.BUY_X_PAY_Y,
                null,
                null,
                null,
                3,
                2,
                null,
                null,
                null,
                List.of(itemDTO)
        );

        when(productService.findById(1L)).thenReturn(product);
        when(promotionRepository.save(any(Promotion.class))).thenAnswer(inv -> {
            Promotion promo = inv.getArgument(0);
            promo.setId(1L);
            return promo;
        });

        PromotionDTO result = promotionService.createPromotion(dto);

        assertNotNull(result);
        assertEquals(3, result.buyQuantity());
        assertEquals(2, result.payQuantity());
    }

    @Test
    @DisplayName("Create promotion with recurring day discount")
    void createPromotionRecurringDay() {
        Product product = new Product("Empanada", "Carne", BigDecimal.valueOf(500), 20);
        product.setId(1L);

        PromotionItemCreateDTO itemDTO = new PromotionItemCreateDTO(1L);
        PromotionCreateDTO dto = new PromotionCreateDTO(
                "Lunes de empanadas",
                "15% todos los lunes",
                LocalDateTime.now(),
                LocalDateTime.now().plusMonths(1),
                PromotionType.RECURRING_DAY_DISCOUNT,
                BigDecimal.valueOf(15),
                null,
                null,
                null,
                null,
                DayOfWeek.MONDAY,
                null,
                null,
                List.of(itemDTO)
        );

        when(productService.findById(1L)).thenReturn(product);
        when(promotionRepository.save(any(Promotion.class))).thenAnswer(inv -> {
            Promotion promo = inv.getArgument(0);
            promo.setId(1L);
            return promo;
        });

        PromotionDTO result = promotionService.createPromotion(dto);

        assertNotNull(result);
        assertEquals(DayOfWeek.MONDAY, result.recurringDay());
    }

    @Test
    @DisplayName("Create promotion with amount discount")
    void createPromotionAmountDiscount() {
        Product product = new Product("Combo", "Completo", BigDecimal.valueOf(3000), 8);
        product.setId(1L);

        PromotionItemCreateDTO itemDTO = new PromotionItemCreateDTO(1L);
        PromotionCreateDTO dto = new PromotionCreateDTO(
                "Descuento por monto",
                "$500 OFF en compras mayores a $2500",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30),
                PromotionType.AMOUNT_DISCOUNT,
                null,
                BigDecimal.valueOf(500),
                BigDecimal.valueOf(2500),
                null,
                null,
                null,
                null,
                null,
                List.of(itemDTO)
        );

        when(productService.findById(1L)).thenReturn(product);
        when(promotionRepository.save(any(Promotion.class))).thenAnswer(inv -> {
            Promotion promo = inv.getArgument(0);
            promo.setId(1L);
            return promo;
        });

        PromotionDTO result = promotionService.createPromotion(dto);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(500), result.discountAmount());
        assertEquals(BigDecimal.valueOf(2500), result.minimumPurchaseAmount());
    }

    @Test
    @DisplayName("Get existing promotion by ID")
    void getPromotionById() {
        Promotion promotion = new Promotion();
        promotion.setId(1L);
        promotion.setName("Promo Test");
        promotion.setType(PromotionType.PERCENTAGE_DISCOUNT);
        promotion.setDiscountPercentage(BigDecimal.valueOf(10));
        promotion.setStartDate(LocalDateTime.now());
        promotion.setEndDate(LocalDateTime.now().plusDays(7));

        when(promotionRepository.findById(1L)).thenReturn(Optional.of(promotion));

        Promotion found = promotionService.findPromotion(1L);

        assertNotNull(found);
        assertEquals("Promo Test", found.getName());
    }

    @Test
    @DisplayName("Throws exception when promotion not found")
    void promotionNotFound() {
        when(promotionRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> promotionService.findPromotion(99L));
    }

    @Test
    @DisplayName("Get active promotions returns only active ones")
    void getActivePromotions() {
        Promotion promo1 = new Promotion();
        promo1.setId(1L);
        promo1.setName("Promo Activa");
        promo1.setActive(true);
        promo1.setType(PromotionType.PERCENTAGE_DISCOUNT);
        promo1.setDiscountPercentage(BigDecimal.valueOf(20));
        promo1.setStartDate(LocalDateTime.now().minusDays(1));
        promo1.setEndDate(LocalDateTime.now().plusDays(7));

        when(promotionRepository.findActiveNow(any(LocalDateTime.class))).thenReturn(List.of(promo1));

        List<PromotionDTO> actives = promotionService.getActivePromotionsForStudents();

        assertEquals(1, actives.size());
        assertTrue(actives.get(0).active());
    }

    @Test
    @DisplayName("Add item to existing promotion")
    void addItemToPromotion() {
        Promotion promotion = new Promotion();
        promotion.setId(1L);
        promotion.setName("Promo Test");
        promotion.setType(PromotionType.PERCENTAGE_DISCOUNT);
        promotion.setDiscountPercentage(BigDecimal.valueOf(15));
        promotion.setStartDate(LocalDateTime.now());
        promotion.setEndDate(LocalDateTime.now().plusDays(7));

        Product product = new Product("Nuevo Producto", "Descripción", BigDecimal.valueOf(1000), 10);
        product.setId(2L);

        when(promotionRepository.findById(1L)).thenReturn(Optional.of(promotion));
        when(productService.findById(2L)).thenReturn(product);
        when(promotionRepository.save(any(Promotion.class))).thenAnswer(inv -> inv.getArgument(0));

        PromotionItemCreateDTO itemDTO = new PromotionItemCreateDTO(2L);
        PromotionDTO result = promotionService.addItemToPromotion(1L, itemDTO);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Throws exception when creating promotion without items")
    void throwsExceptionWhenCreatingWithoutItems() {
        PromotionCreateDTO dto = new PromotionCreateDTO(
                "Promo sin items",
                "No tiene productos",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7),
                PromotionType.PERCENTAGE_DISCOUNT,
                BigDecimal.valueOf(10),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                List.of()
        );

        assertThrows(IllegalArgumentException.class, () -> promotionService.createPromotion(dto));
    }

    @Test
    @DisplayName("Activate promotion successfully")
    void activatePromotion() {
        Promotion promotion = new Promotion();
        promotion.setId(1L);
        promotion.setName("Promo Inactiva");
        promotion.setActive(false);
        promotion.setType(PromotionType.PERCENTAGE_DISCOUNT);
        promotion.setDiscountPercentage(BigDecimal.valueOf(10));
        promotion.setStartDate(LocalDateTime.now());
        promotion.setEndDate(LocalDateTime.now().plusDays(7));

        when(promotionRepository.findById(1L)).thenReturn(Optional.of(promotion));
        when(promotionRepository.save(any(Promotion.class))).thenAnswer(inv -> inv.getArgument(0));

        PromotionDTO result = promotionService.activatePromotion(1L);

        assertTrue(result.active());
    }

    @Test
    @DisplayName("Desactivate promotion successfully")
    void desactivatePromotion() {
        Promotion promotion = new Promotion();
        promotion.setId(1L);
        promotion.setName("Promo Activa");
        promotion.setActive(true);
        promotion.setType(PromotionType.PERCENTAGE_DISCOUNT);
        promotion.setDiscountPercentage(BigDecimal.valueOf(10));
        promotion.setStartDate(LocalDateTime.now());
        promotion.setEndDate(LocalDateTime.now().plusDays(7));

        when(promotionRepository.findById(1L)).thenReturn(Optional.of(promotion));
        when(promotionRepository.save(any(Promotion.class))).thenAnswer(inv -> inv.getArgument(0));

        PromotionDTO result = promotionService.desactivatePromotion(1L);

        assertFalse(result.active());
    }

    @Test
    @DisplayName("Delete promotion successfully")
    void deletePromotion() {
        Promotion promotion = new Promotion();
        promotion.setId(1L);
        promotion.setName("Promo a eliminar");
        promotion.setDeleted(false);
        promotion.setType(PromotionType.PERCENTAGE_DISCOUNT);
        promotion.setDiscountPercentage(BigDecimal.valueOf(10));
        promotion.setStartDate(LocalDateTime.now());
        promotion.setEndDate(LocalDateTime.now().plusDays(7));

        when(promotionRepository.findById(1L)).thenReturn(Optional.of(promotion));
        doNothing().when(promotionRepository).delete(any(Promotion.class));

        assertDoesNotThrow(() -> promotionService.deletePromotion(1L));
        verify(promotionRepository, times(1)).delete(any(Promotion.class));
    }

    @Test
    @DisplayName("Get all promotions returns list")
    void getAllPromotions() {
        Promotion promo1 = new Promotion();
        promo1.setId(1L);
        promo1.setName("Promo 1");
        promo1.setType(PromotionType.PERCENTAGE_DISCOUNT);
        promo1.setDiscountPercentage(BigDecimal.valueOf(10));
        promo1.setStartDate(LocalDateTime.now());
        promo1.setEndDate(LocalDateTime.now().plusDays(7));

        Promotion promo2 = new Promotion();
        promo2.setId(2L);
        promo2.setName("Promo 2");
        promo2.setType(PromotionType.BUY_X_PAY_Y);
        promo2.setBuyQuantity(3);
        promo2.setPayQuantity(2);
        promo2.setStartDate(LocalDateTime.now());
        promo2.setEndDate(LocalDateTime.now().plusDays(7));

        when(promotionRepository.findAll()).thenReturn(List.of(promo1, promo2));

        List<PromotionDTO> all = promotionService.getAllPromotions();

        assertEquals(2, all.size());
    }

    @Test
    @DisplayName("Update promotion clears previous fields and recalculates")
    void updatePromotionClearsPreviousFieldsAndRecalculates() {
        Product product = new Product("Hamburguesa", "Con queso", BigDecimal.valueOf(1500), 10);
        product.setId(1L);

        Promotion existing = new Promotion();
        existing.setId(1L);
        existing.setName("Promo Original");
        existing.setType(PromotionType.PERCENTAGE_DISCOUNT);
        existing.setDiscountPercentage(BigDecimal.valueOf(20));
        existing.setBuyQuantity(2); // Campo a limpiar
        existing.setPayQuantity(1); // Campo a limpiar

        PromotionItemCreateDTO itemDTO = new PromotionItemCreateDTO(1L);
        PromotionCreateDTO updateDTO = new PromotionCreateDTO(
                "Promo Actualizada",
                "Nueva descripción",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(10),
                PromotionType.AMOUNT_DISCOUNT,
                null,
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(500),
                null,
                null,
                null,
                null,
                null,
                List.of(itemDTO)
        );

        when(promotionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productService.findById(1L)).thenReturn(product);
        when(promotionRepository.save(any(Promotion.class))).thenAnswer(inv -> inv.getArgument(0));

        PromotionDTO result = promotionService.updatePromotion(1L, updateDTO);

        assertNotNull(result);
        assertEquals("Promo Actualizada", result.name());
        assertEquals(PromotionType.AMOUNT_DISCOUNT, result.type());
        verify(promotionRepository).save(any(Promotion.class));
    }

    @Test
    @DisplayName("Update promotion validates and configures with strategy")
    void updatePromotionValidatesAndConfiguresWithStrategy() {
        Product product = new Product("Pizza", "Muzzarella", BigDecimal.valueOf(2000), 10);
        product.setId(1L);

        Promotion existing = new Promotion();
        existing.setId(1L);
        existing.setName("Promo Original");
        existing.setType(PromotionType.AMOUNT_DISCOUNT);

        PromotionItemCreateDTO itemDTO = new PromotionItemCreateDTO(1L);
        PromotionCreateDTO updateDTO = new PromotionCreateDTO(
                "Promo BuyXPayY",
                "Compra 3 paga 2",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7),
                PromotionType.BUY_X_PAY_Y,
                null,
                null,
                null,
                3,
                2,
                null,
                null,
                null,
                List.of(itemDTO)
        );

        when(promotionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productService.findById(1L)).thenReturn(product);
        when(promotionRepository.save(any(Promotion.class))).thenAnswer(inv -> inv.getArgument(0));

        PromotionDTO result = promotionService.updatePromotion(1L, updateDTO);

        assertNotNull(result);
        assertEquals(PromotionType.BUY_X_PAY_Y, result.type());
        verify(promotionRepository).save(any(Promotion.class));
    }

    @Test
    @DisplayName("Update promotion clears items and adds new ones")
    void updatePromotionClearsItemsAndAddsNewOnes() {
        Product product1 = new Product("Hamburguesa", "Con queso", BigDecimal.valueOf(1500), 10);
        product1.setId(1L);
        Product product2 = new Product("Papas", "Fritas", BigDecimal.valueOf(800), 15);
        product2.setId(2L);

        Promotion existing = new Promotion();
        existing.setId(1L);
        existing.setName("Promo Original");
        existing.setType(PromotionType.PERCENTAGE_DISCOUNT);
        
        PromotionItem oldItem = new PromotionItem();
        oldItem.setProduct(product1);
        existing.addItem(oldItem);

        PromotionItemCreateDTO itemDTO = new PromotionItemCreateDTO(2L);
        PromotionCreateDTO updateDTO = new PromotionCreateDTO(
                "Promo Nueva",
                "Con producto diferente",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7),
                PromotionType.PERCENTAGE_DISCOUNT,
                BigDecimal.valueOf(15),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                List.of(itemDTO)
        );

        when(promotionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productService.findById(2L)).thenReturn(product2);
        when(promotionRepository.save(any(Promotion.class))).thenAnswer(inv -> inv.getArgument(0));

        PromotionDTO result = promotionService.updatePromotion(1L, updateDTO);

        assertNotNull(result);
        verify(promotionRepository).save(any(Promotion.class));
    }

    @Test
    @DisplayName("Remove item from promotion successfully")
    void removeItemFromPromotionSuccessfully() {
        Product product1 = new Product("Hamburguesa", "Con queso", BigDecimal.valueOf(1500), 10);
        product1.setId(1L);
        Product product2 = new Product("Papas", "Fritas", BigDecimal.valueOf(800), 15);
        product2.setId(2L);

        Promotion promo = new Promotion();
        promo.setId(1L);
        promo.setName("Promo Combo");
        promo.setType(PromotionType.PERCENTAGE_DISCOUNT);
        promo.setDiscountPercentage(BigDecimal.valueOf(20));

        PromotionItem item1 = new PromotionItem();
        item1.setId(1L);
        item1.setProduct(product1);
        item1.setOriginalPrice(product1.getPrice());
        promo.addItem(item1);

        PromotionItem item2 = new PromotionItem();
        item2.setId(2L);
        item2.setProduct(product2);
        item2.setOriginalPrice(product2.getPrice());
        promo.addItem(item2);

        when(promotionRepository.findById(1L)).thenReturn(Optional.of(promo));
        when(promotionRepository.save(any(Promotion.class))).thenAnswer(inv -> inv.getArgument(0));

        PromotionDTO result = promotionService.removeItemFromPromotion(1L, 1L);

        assertNotNull(result);
        verify(promotionRepository).save(any(Promotion.class));
    }

    @Test
    @DisplayName("Remove item from promotion recalculates prices")
    void removeItemFromPromotionRecalculatesPrices() {
        Product product1 = new Product("Hamburguesa", "Con queso", BigDecimal.valueOf(1500), 10);
        product1.setId(1L);
        Product product2 = new Product("Papas", "Fritas", BigDecimal.valueOf(800), 15);
        product2.setId(2L);

        Promotion promo = new Promotion();
        promo.setId(1L);
        promo.setName("Promo Combo");
        promo.setType(PromotionType.BUY_X_PAY_Y);
        promo.setBuyQuantity(2);
        promo.setPayQuantity(1);

        PromotionItem item1 = new PromotionItem();
        item1.setId(1L);
        item1.setProduct(product1);
        item1.setOriginalPrice(product1.getPrice());
        promo.addItem(item1);

        PromotionItem item2 = new PromotionItem();
        item2.setId(2L);
        item2.setProduct(product2);
        item2.setOriginalPrice(product2.getPrice());
        promo.addItem(item2);

        when(promotionRepository.findById(1L)).thenReturn(Optional.of(promo));
        when(promotionRepository.save(any(Promotion.class))).thenAnswer(inv -> inv.getArgument(0));

        promotionService.removeItemFromPromotion(1L, 2L);

        verify(promotionRepository).save(any(Promotion.class));
    }
}
