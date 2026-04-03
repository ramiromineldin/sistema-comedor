package ar.uba.fi.ingsoft1.product_example.ingredients;

import ar.uba.fi.ingsoft1.product_example.user.User;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.function.Function;

public record IngredientCreateDTO (
    @NotBlank String name,
    @NotBlank String description,
    @NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal price,
    @Min(0) Integer initialStock
) {
    public Ingredient asIngrdient() {
        return new Ingredient(name, description, price, initialStock);
    }
}
