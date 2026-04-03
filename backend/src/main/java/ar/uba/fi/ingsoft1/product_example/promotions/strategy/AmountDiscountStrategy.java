package ar.uba.fi.ingsoft1.product_example.promotions.strategy;

import ar.uba.fi.ingsoft1.product_example.promotions.Promotion;
import ar.uba.fi.ingsoft1.product_example.promotions.dto.PromotionCreateDTO;

import java.math.BigDecimal;

public class AmountDiscountStrategy implements PromotionStrategy {
    @Override
    public void configure(Promotion promotion, PromotionCreateDTO request) {
        promotion.setDiscountAmount(request.discountAmount());
        promotion.setMinimumPurchaseAmount(request.minimumPurchaseAmount());
    }

    @Override
    public void validate(PromotionCreateDTO request) {
        if (request.discountAmount() == null || request.discountAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto de descuento es obligatorio y debe ser mayor a 0");
        }
        if (request.minimumPurchaseAmount() == null || request.minimumPurchaseAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto mínimo de compra es obligatorio y debe ser mayor a 0");
        }
    }

    @Override
    public BigDecimal calculateFinalPrice(Promotion promotion, BigDecimal originalPrice) {
        if (originalPrice == null) return BigDecimal.ZERO;

        BigDecimal min = promotion.getMinimumPurchaseAmount();
        if (min != null && originalPrice.compareTo(min) < 0) {
            return originalPrice; // no aplica descuento
        }

        BigDecimal amountOff = promotion.getDiscountAmount();
        if (amountOff == null) return originalPrice;

        BigDecimal result = originalPrice.subtract(amountOff);
        return result.compareTo(BigDecimal.ZERO) > 0 ? result : BigDecimal.ZERO;
    }
}
