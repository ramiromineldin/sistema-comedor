package ar.uba.fi.ingsoft1.product_example.config;

import ar.uba.fi.ingsoft1.product_example.products.Product;
import ar.uba.fi.ingsoft1.product_example.products.ProductRepository;
import ar.uba.fi.ingsoft1.product_example.promotions.Promotion;
import ar.uba.fi.ingsoft1.product_example.promotions.PromotionItem;
import ar.uba.fi.ingsoft1.product_example.promotions.PromotionRepository;
import ar.uba.fi.ingsoft1.product_example.promotions.PromotionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PromotionsInitializer {

    private final PromotionRepository promotionRepository;
    private final ProductRepository productRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializePromotions() {
        log.info("Inicializando promociones del comedor...");
        
        // Verificar si ya existen promociones
        if (promotionRepository.count() > 0) {
            log.info("Las promociones ya están inicializadas.");
            return;
        }

        try {
            create3x2AlfajoresPromotion();
            createEnsaladasMartesPromotion();
            createDescuentoComprasMayoresPromotion();
            createComboAlmuerzoPromotion();
            log.info("Promociones inicializadas exitosamente.");
        } catch (Exception e) {
            log.error("Error al inicializar promociones: ", e);
        }
    }

    private void create3x2AlfajoresPromotion() {
        // Promoción: Lleva 3 alfajores y paga 2
        Promotion alfajores3x2 = new Promotion();
        alfajores3x2.setName("3x2 en Alfajores");
        alfajores3x2.setDescription("Llevá 3 alfajores y pagá solo 2. ¡Aprovechá esta oferta dulce!");
        alfajores3x2.setType(PromotionType.BUY_X_PAY_Y);
        alfajores3x2.setBuyQuantity(3);
        alfajores3x2.setPayQuantity(2);
        alfajores3x2.setStartDate(LocalDateTime.now());
        alfajores3x2.setEndDate(LocalDateTime.now().plusMonths(3));
        alfajores3x2.setActive(true);

        // Crear diferentes tipos de alfajores
        List<String> alfajoresProducts = List.of("Alfajor Jorgito", "Alfajor Jorgito", "Alfajor Jorgito");
        for (String productName : alfajoresProducts) {
            findOrCreateProduct(productName, "Alfajor argentino", BigDecimal.valueOf(1200))
                    .ifPresent(product -> {
                        PromotionItem item = new PromotionItem();
                        item.setProduct(product);
                        item.setOriginalPrice(product.getPrice());
                        alfajores3x2.addItem(item);
                    });
        }

        alfajores3x2.calculatePrices();
        if (!alfajores3x2.getItems().isEmpty()) {
            promotionRepository.save(alfajores3x2);
            log.info("Promoción 3x2 alfajores creada con {} productos", alfajores3x2.getItems().size());
        }
    }

    private void createEnsaladasMartesPromotion() {
        // Promoción: 20% de descuento en todas las ensaladas los martes
        Promotion ensaladasMartes = new Promotion();
        ensaladasMartes.setName("Martes de Ensaladas");
        ensaladasMartes.setDescription("20% de descuento en todas las ensaladas todos los martes. ¡Comé sano y ahorrá!");
        ensaladasMartes.setType(PromotionType.RECURRING_DAY_DISCOUNT);
        ensaladasMartes.setDiscountPercentage(BigDecimal.valueOf(20.0));
        ensaladasMartes.setRecurringDay(DayOfWeek.TUESDAY);
        ensaladasMartes.setStartDate(LocalDateTime.now());
        ensaladasMartes.setEndDate(LocalDateTime.now().plusYears(1));
        ensaladasMartes.setActive(true);

        // Crear diferentes ensaladas
        List<String> ensaladasProducts = List.of(
                "Ensalada César",
                "Ensalada Caprese",
                "Ensalada Mixta",
                "Ensalada de Rúcula y Parmesano"
        );
        
        for (String productName : ensaladasProducts) {
            BigDecimal price = productName.contains("Rúcula") ? BigDecimal.valueOf(4500) : BigDecimal.valueOf(3800);
            String description = switch (productName) {
                case "Ensalada César" -> "Ensalada con lechuga, pollo grillado, crutones, queso parmesano y salsa césar";
                case "Ensalada Caprese" -> "Ensalada con tomate, muzzarella, albahaca fresca y aceite de oliva";
                case "Ensalada Mixta" -> "Ensalada con lechuga, tomate, zanahoria, huevo duro y aceite de oliva";
                case "Ensalada de Rúcula y Parmesano" -> "Ensalada con rúcula, queso parmesano, tomates cherry, aceite de oliva y aceto balsámico";
                default -> "Ensalada fresca del día";
            };
            findOrCreateProduct(productName, description, price)
                    .ifPresent(product -> {
                        PromotionItem item = new PromotionItem();
                        item.setProduct(product);
                        item.setOriginalPrice(product.getPrice());
                        ensaladasMartes.addItem(item);
                    });
        }

        ensaladasMartes.calculatePrices();
        if (!ensaladasMartes.getItems().isEmpty()) {
            promotionRepository.save(ensaladasMartes);
            log.info("Promoción martes de ensaladas creada con {} productos", ensaladasMartes.getItems().size());
        }
    }

    private void createDescuentoComprasMayoresPromotion() {
        // Promoción: $5000 de descuento en compras mayores a $30000
        Promotion comprasMayores = new Promotion();
        comprasMayores.setName("Descuento de $5000 por Compra Mayor a $30000");
        comprasMayores.setDescription("$5000 de descuento en compras superiores a $30000. ¡Ideal para grupos!");
        comprasMayores.setType(PromotionType.AMOUNT_DISCOUNT);
        comprasMayores.setDiscountAmount(BigDecimal.valueOf(5000));
        comprasMayores.setMinimumPurchaseAmount(BigDecimal.valueOf(30000));
        comprasMayores.setStartDate(LocalDateTime.now());
        comprasMayores.setEndDate(LocalDateTime.now().plusMonths(6));
        comprasMayores.setActive(true);

        // Agregar productos variados del comedor para alcanzar el mínimo
        List<ProductData> productos = List.of(
                new ProductData("Hamburguesa Completa", "Medallón, pan, queso, lechuga, tomate y huevo", BigDecimal.valueOf(4500)),
                new ProductData("Milanesa Napolitana", "Milanesa de carne con jamón, queso, salsa de tomate y papas fritas", BigDecimal.valueOf(5500)),
                new ProductData("Pizza Muzzarella", "Pizza grande (8 porciones) con masa, salsa de tomate, muzzarella y aceitunas", BigDecimal.valueOf(6000)),
                new ProductData("Empanadas x 6", "Media docena de empanadas caseras de carne, pollo o jamón y queso", BigDecimal.valueOf(3600)),
                new ProductData("Sándwich de Milanesa", "Pan, milanesa, lechuga, tomate, queso, huevo, mayonesa", BigDecimal.valueOf(3200)),
                new ProductData("Pancho Completo", "Pan para pancho, salchicha con papas fritas", BigDecimal.valueOf(2800)),
                new ProductData("Coca Cola 1.5L", "Gaseosa Coca Cola 1.5 litros", BigDecimal.valueOf(1800)),
                new ProductData("Agua Mineral", "Agua mineral sin gas 500ml", BigDecimal.valueOf(800))
        );

        for (ProductData productData : productos) {
            findOrCreateProduct(productData.name, productData.description, productData.price)
                    .ifPresent(product -> {
                        PromotionItem item = new PromotionItem();
                        item.setProduct(product);
                        item.setOriginalPrice(product.getPrice());
                        comprasMayores.addItem(item);
                    });
        }

        comprasMayores.calculatePrices();
        if (!comprasMayores.getItems().isEmpty()) {
            promotionRepository.save(comprasMayores);
            log.info("Promoción de compras mayores creada con {} productos", comprasMayores.getItems().size());
        }
    }

    private void createComboAlmuerzoPromotion() {
        // Promoción: Combo almuerzo con 15% de descuento
        Promotion comboAlmuerzo = new Promotion();
        comboAlmuerzo.setName("Combo Almuerzo Estudiante");
        comboAlmuerzo.setDescription("15% de descuento en tu almuerzo completo: plato principal + bebida + postre");
        comboAlmuerzo.setType(PromotionType.PERCENTAGE_DISCOUNT);
        comboAlmuerzo.setDiscountPercentage(BigDecimal.valueOf(15.0));
        comboAlmuerzo.setStartDate(LocalDateTime.now());
        comboAlmuerzo.setEndDate(LocalDateTime.now().plusMonths(12));
        comboAlmuerzo.setActive(true);

        // Platos principales
        List<ProductData> platosYBebidas = List.of(
                new ProductData("Hamburguesa Completa", "Medallón, pan, queso, lechuga, tomate y huevo", BigDecimal.valueOf(4500)),
                new ProductData("Milanesa Napolitana", "Milanesa de carne con jamón, queso, salsa de tomate y papas fritas", BigDecimal.valueOf(5500)),
                new ProductData("Milanesa con papas fritas", "Milanesa de carne con guarnición de papas fritas", BigDecimal.valueOf(4200)),
                new ProductData("Coca Cola 1.5L", "Gaseosa Coca Cola 1.5 litros", BigDecimal.valueOf(1800)),
                new ProductData("Agua Mineral", "Agua mineral sin gas 500ml", BigDecimal.valueOf(800)),
                new ProductData("Alfajor Jorgito", "Alfajor argentino", BigDecimal.valueOf(1200)),
                new ProductData("Alfajor Terrabusi", "Alfajor argentino", BigDecimal.valueOf(1200)),
                new ProductData("Alfajor Havanna", "Alfajor argentino", BigDecimal.valueOf(1200))
        );

        for (ProductData productData : platosYBebidas) {
            findOrCreateProduct(productData.name, productData.description, productData.price)
                    .ifPresent(product -> {
                        PromotionItem item = new PromotionItem();
                        item.setProduct(product);
                        item.setOriginalPrice(product.getPrice());
                        comboAlmuerzo.addItem(item);
                    });
        }

        comboAlmuerzo.calculatePrices();
        if (!comboAlmuerzo.getItems().isEmpty()) {
            promotionRepository.save(comboAlmuerzo);
            log.info("Promoción combo almuerzo creada con {} productos", comboAlmuerzo.getItems().size());
        }
    }

    private Optional<Product> findOrCreateProduct(String name, String description, BigDecimal defaultPrice) {
        Optional<Product> existing = productRepository.findByName(name);
        if (existing.isPresent()) {
            return existing;
        }

        try {
            Product newProduct = new Product();
            newProduct.setName(name);
            newProduct.setDescription(description);
            newProduct.setPrice(defaultPrice);
            newProduct.setStock(20); 
            
            Product saved = productRepository.save(newProduct);
            log.info("Producto '{}' creado para promoción", name);
            return Optional.of(saved);
        } catch (Exception e) {
            log.warn("No se pudo crear el producto '{}' para la promoción: {}", name, e.getMessage());
            return Optional.empty();
        }
    }

    // Clase auxiliar para datos de productos
    private static class ProductData {
        String name;
        String description;
        BigDecimal price;

        ProductData(String name, String description, BigDecimal price) {
            this.name = name;
            this.description = description;
            this.price = price;
        }
    }
}
