package ar.uba.fi.ingsoft1.product_example.promotions.strategy;

import ar.uba.fi.ingsoft1.product_example.promotions.Promotion;
import ar.uba.fi.ingsoft1.product_example.promotions.dto.PromotionCreateDTO;

import java.math.BigDecimal;

public class PercentageDiscountStrategy implements PromotionStrategy {
    @Override
    public void configure(Promotion promotion, PromotionCreateDTO request) {
        promotion.setDiscountPercentage(request.discountPercentage());
    }

    @Override
    public void validate(PromotionCreateDTO request) {
        if (request.discountPercentage() == null ||
                request.discountPercentage().compareTo(BigDecimal.ZERO) <= 0 ||
                request.discountPercentage().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("El porcentaje de descuento debe estar entre 0 y 100");
        }
    }

    @Override
    public BigDecimal calculateFinalPrice(Promotion promotion, BigDecimal originalPrice) {
        if (originalPrice == null || originalPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount = promotion.getDiscountPercentage();
        if (discount == null) {
            return originalPrice;
        }

        // Aplica el porcentaje de descuento: precio * (1 - descuento/100)
        BigDecimal multiplier = BigDecimal.valueOf(100).subtract(discount)
                .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
        return originalPrice.multiply(multiplier).setScale(2, java.math.RoundingMode.HALF_UP);
    }
}
