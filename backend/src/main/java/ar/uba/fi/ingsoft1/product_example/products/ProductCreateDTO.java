package ar.uba.fi.ingsoft1.product_example.products;

import lombok.NonNull;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;

record ProductCreateDTO(
        @Validated @NonNull String name,
        @Validated @NonNull String description,
        @Validated @NonNull BigDecimal price,
        List<Long> ingredientesIds
) {
    public Product asProduct() {
        return new Product(name, description, price, 0);
    }
}