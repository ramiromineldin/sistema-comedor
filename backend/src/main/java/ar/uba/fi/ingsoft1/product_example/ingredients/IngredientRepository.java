package ar.uba.fi.ingsoft1.product_example.ingredients;

import ar.uba.fi.ingsoft1.product_example.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    Optional<Ingredient> findByNameIgnoreCase(String name);
}
