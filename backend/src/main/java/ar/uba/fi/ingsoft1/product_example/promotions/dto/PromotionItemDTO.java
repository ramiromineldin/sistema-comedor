package ar.uba.fi.ingsoft1.product_example.promotions.dto;

import ar.uba.fi.ingsoft1.product_example.promotions.PromotionItem;
import ar.uba.fi.ingsoft1.product_example.products.Product;

import java.math.BigDecimal;

public record PromotionItemDTO (
        Long productId,
        String productName,
        BigDecimal originalPrice,
        boolean inStock
) {
    public static PromotionItemDTO fromPromotionItem(PromotionItem item) {
        Product product = item.getProduct();
        return new PromotionItemDTO(
                product.getId(),
                product.getName(),
                item.getOriginalPrice(),
                product.hasStock()
        );
    }
}
