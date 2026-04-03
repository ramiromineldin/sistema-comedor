package ar.uba.fi.ingsoft1.product_example.products;

import ar.uba.fi.ingsoft1.product_example.alimentos.TipoAlimento;

import java.math.BigDecimal;

public record ProductDTO(
        long id,
        String name,
        String description,
        BigDecimal price,
        boolean available,
        Integer currentStock,
        boolean hasStock
) {
    public ProductDTO(Product product) {
        this(product.getId(), product.getName(), product.getDescription(), 
             product.getPrice(), product.isAvailable(), product.getCurrentStock(),
             product.hasStock());
    }
}
