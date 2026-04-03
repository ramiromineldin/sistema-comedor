package ar.uba.fi.ingsoft1.product_example.promotions.strategy;

import ar.uba.fi.ingsoft1.product_example.promotions.Promotion;
import ar.uba.fi.ingsoft1.product_example.promotions.dto.PromotionCreateDTO;

import java.math.BigDecimal;

public class BuyXPayYStrategy implements PromotionStrategy {
    @Override
    public void configure(Promotion promotion, PromotionCreateDTO request) {
        promotion.setBuyQuantity(request.buyQuantity());
        promotion.setPayQuantity(request.payQuantity());
    }

    @Override
    public void validate(PromotionCreateDTO request) {
        if (request.buyQuantity() == null || request.buyQuantity() <= 0) {
            throw new IllegalArgumentException("La cantidad a llevar debe ser mayor a 0");
        }
        if (request.payQuantity() == null || request.payQuantity() <= 0) {
            throw new IllegalArgumentException("La cantidad a pagar debe ser mayor a 0");
        }
        if (request.payQuantity() >= request.buyQuantity()) {
            throw new IllegalArgumentException("La cantidad a pagar debe ser menor a la cantidad a llevar");
        }
    }

    @Override
    public BigDecimal calculateFinalPrice(Promotion promotion, BigDecimal originalPrice) {
        if (originalPrice == null || originalPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        Integer buy = promotion.getBuyQuantity();
        Integer pay = promotion.getPayQuantity();
        
        if (buy == null || buy <= 0 || pay == null || pay <= 0) {
            return originalPrice;
        }

        
        
        return originalPrice
            .multiply(BigDecimal.valueOf(pay))
            .divide(BigDecimal.valueOf(buy), 2, java.math.RoundingMode.HALF_UP);
    }
}
