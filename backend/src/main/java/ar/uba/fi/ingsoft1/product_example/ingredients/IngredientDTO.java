package ar.uba.fi.ingsoft1.product_example.ingredients;

import jakarta.persistence.criteria.CriteriaBuilder;

import java.math.BigDecimal;

public record IngredientDTO(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stock
) {
    public static IngredientDTO fromIngredient(Ingredient ingredient) {
        return new IngredientDTO(
                ingredient.getId(),
                ingredient.getName(),
                ingredient.getDescription(),
                ingredient.getPrice(),
                ingredient.getCurrentStock()
        );
    }
}
