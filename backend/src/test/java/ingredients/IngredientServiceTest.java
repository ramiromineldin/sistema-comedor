package ingredients;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ar.uba.fi.ingsoft1.product_example.ingredients.Ingredient;
import ar.uba.fi.ingsoft1.product_example.ingredients.IngredientCreateDTO;
import ar.uba.fi.ingsoft1.product_example.ingredients.IngredientDTO;
import ar.uba.fi.ingsoft1.product_example.ingredients.IngredientRepository;
import ar.uba.fi.ingsoft1.product_example.ingredients.IngredientService;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class IngredientServiceTest {

    private IngredientRepository ingredientRepository;

    private IngredientService ingredientService;

    @BeforeEach
    void setup() {
        ingredientRepository = mock(IngredientRepository.class);

        ingredientService = new IngredientService(ingredientRepository);
    }


    @Test
    @DisplayName("Create ingredient with valid data")
    void createIngredientSuccessfully() {
        IngredientCreateDTO dto = new IngredientCreateDTO(
                "Tomate", "Fresco y maduro", BigDecimal.valueOf(30.0), 10
        );

        Ingredient saved = new Ingredient("Tomate", "Fresco y maduro", BigDecimal.valueOf(30.0), 10);
        saved.setId(1L);

        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(saved);

        IngredientDTO result = ingredientService.createIngredient(dto);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Get existing ingredient by ID")
    void getIngredientById() {
        Ingredient ingrediente = new Ingredient("Lechuga", "Verde", BigDecimal.valueOf(20), 5);
        ingrediente.setId(5L);

        when(ingredientRepository.findById(5L)).thenReturn(Optional.of(ingrediente));

        IngredientDTO found = ingredientService.getIngredientbyId(5L);

        assertEquals("Lechuga", found.name());
    }

    @Test
    @DisplayName("Increment stock correctly")
    void incrementStock() {
        Ingredient ingrediente = new Ingredient("Papa", "negra", BigDecimal.valueOf(10), 3);
        ingrediente.setId(2L);

        when(ingredientRepository.findById(2L)).thenReturn(Optional.of(ingrediente));
        when(ingredientRepository.save(any(Ingredient.class))).thenAnswer(inv -> inv.getArgument(0));

        Ingredient updated = ingredientService.increment(2L, 5);

        assertEquals(8, updated.getCurrentStock());
    }

    @Test
    @DisplayName("Decrement stock correctly until 0")
    void decrementStockToZero() {
        Ingredient ingrediente = new Ingredient("Zanahoria", "Naranja", BigDecimal.valueOf(15), 2);
        ingrediente.setId(3L);

        when(ingredientRepository.findById(3L)).thenReturn(Optional.of(ingrediente));
        when(ingredientRepository.save(any(Ingredient.class))).thenAnswer(inv -> inv.getArgument(0));

        Ingredient updated = ingredientService.decrement(3L, 5);

        assertEquals(0, updated.getCurrentStock());
    }

    @Test
    @DisplayName("Throws exception if an ingredient is searched by id and doesnt exist")
    void ingredientNotFound() {
        when(ingredientRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> ingredientService.increment(99L, 3));
        
    }
}
