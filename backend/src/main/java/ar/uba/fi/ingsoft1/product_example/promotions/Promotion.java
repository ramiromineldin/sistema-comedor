package ar.uba.fi.ingsoft1.product_example.promotions;

import ar.uba.fi.ingsoft1.product_example.promotions.dto.PromotionCreateDTO;
import ar.uba.fi.ingsoft1.product_example.promotions.strategy.PromotionStrategy;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "promotions")
@Data
@NoArgsConstructor
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean deleted = false;

    // Tipo de promoción
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PromotionType type;

    // Para PERCENTAGE_DISCOUNT: porcentaje de descuento (ej: 20 para 20%)
    @Column(precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    // Para AMOUNT_DISCOUNT: monto de descuento fijo
    @Column(precision = 10, scale = 2)
    private BigDecimal discountAmount;

    // Para AMOUNT_DISCOUNT: monto mínimo de compra
    @Column(precision = 10, scale = 2)
    private BigDecimal minimumPurchaseAmount;

    // Para BUY_X_PAY_Y: cantidad a comprar
    @Column
    private Integer buyQuantity;

    // Para BUY_X_PAY_Y: cantidad a pagar
    @Column
    private Integer payQuantity;

    // Para RECURRING_DAY_DISCOUNT: día de la semana en que aplica
    @Enumerated(EnumType.STRING)
    @Column(name = "recurring_day")
    private java.time.DayOfWeek recurringDay;

    // Para TIME_RESTRICTED_BUY_X_PAY_Y: hora de inicio de la promoción
    @Column(name = "start_time")
    private java.time.LocalTime startTime;

    // Para TIME_RESTRICTED_BUY_X_PAY_Y: hora de fin de la promoción
    @Column(name = "end_time")
    private java.time.LocalTime endTime;

    // Precio original de la promoción (suma de todos los items)
    @Column(precision = 10, scale = 2)
    private BigDecimal originalPrice;

    // Precio final de la promoción (con descuento aplicado)
    @Column(precision = 10, scale = 2)
    private BigDecimal finalPrice;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    private Set<PromotionItem> items = new HashSet<>();

    public void addItem(PromotionItem item) {
        items.add(item);
    }

    public void removeItem(PromotionItem item) {
        items.remove(item);
    }

    public PromotionStrategy getStrategy() {
        if (type == null) {
            throw new IllegalStateException("El tipo de promoción no puede ser nulo");
        }
        return type.getStrategy();
    }

    public void configureWithStrategy(PromotionCreateDTO request) {
        getStrategy().configure(this, request);
    }

    public void validateWithStrategy(PromotionCreateDTO request) {
        getStrategy().validate(request);
    }

    /**
     * Calcula y actualiza los precios de la promoción
     */
    public void calculatePrices() {
        // Calcular precio base sumando todos los items (precio de 1 unidad/combo)
        BigDecimal basePrice = items.stream()
                .map(PromotionItem::getOriginalPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Para promociones BUY_X_PAY_Y y TIME_RESTRICTED_BUY_X_PAY_Y, 
        // el originalPrice representa comprar buyQuantity unidades
        // Ejemplo: 2x1 café ($800) → originalPrice = $1600 (2 cafés)
        if ((type == PromotionType.BUY_X_PAY_Y || type == PromotionType.TIME_RESTRICTED_BUY_X_PAY_Y) 
            && buyQuantity != null && buyQuantity > 0) {
            this.originalPrice = basePrice.multiply(BigDecimal.valueOf(buyQuantity));
        } else {
            this.originalPrice = basePrice;
        }

        // Calcular precio final usando la estrategia
        this.finalPrice = getStrategy().calculateFinalPrice(this, this.originalPrice);
    }

    @Override
    public String toString() {
        return "Promotion{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", active=" + active +
                ", type=" + type +
                ", itemsCount=" + (items == null ? 0 : items.size()) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Promotion promotion = (Promotion) o;
        if (this.id != null && promotion.id != null) {
            return Objects.equals(this.id, promotion.id);
        }
        return Objects.equals(this.name, promotion.name) &&
                Objects.equals(this.startDate, promotion.startDate) &&
                Objects.equals(this.endDate, promotion.endDate);
    }

    @Override
    public int hashCode() {
        if (id != null) return Objects.hash(id);
        return Objects.hash(name, startDate, endDate);
    }
}