package ar.uba.fi.ingsoft1.product_example.promotions.strategy;

import ar.uba.fi.ingsoft1.product_example.promotions.Promotion;
import ar.uba.fi.ingsoft1.product_example.promotions.dto.PromotionCreateDTO;

import java.math.BigDecimal;

public interface PromotionStrategy {
    void configure(Promotion promotion, PromotionCreateDTO request);
    void validate(PromotionCreateDTO request);
    BigDecimal calculateFinalPrice(Promotion promotion, BigDecimal originalPrice);
}
