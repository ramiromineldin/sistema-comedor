package ar.uba.fi.ingsoft1.product_example.ingredients;

import ar.uba.fi.ingsoft1.product_example.products.ProductDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ingredients")
@Validated
@RequiredArgsConstructor
@Tag(name = "Ingredients")
@PreAuthorize("hasRole('ADMIN') or hasRole('PERSONAL_COCINA')")
public class IngredientRestController {

    private final IngredientService ingredientService;


    @Operation(summary = "Crear un nuevo ingrediente")
    @PostMapping()
    public ResponseEntity<IngredientDTO> create (@Valid @RequestBody IngredientCreateDTO ingredientCreateDTO) {
        IngredientDTO ing = ingredientService.createIngredient(ingredientCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ing);
    }

    @Operation(summary = "Obtener un ingrediente")
    @GetMapping("/{id}")
    public ResponseEntity<IngredientDTO> findById(@PathVariable long id) {
        try {
            IngredientDTO ingredient = ingredientService.getIngredientbyId(id);
            return ResponseEntity.status(HttpStatus.OK).body(ingredient);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Obtener todos los ingredientes")
    @GetMapping
    public ResponseEntity<java.util.List<IngredientDTO>> getAllIngredients() {
        java.util.List<IngredientDTO> ingredients = ingredientService.getAllIngredients();
        return ResponseEntity.ok(ingredients);
    }

    @Operation(summary = "Incrementar stock")
    @PostMapping(value = "/increment/{id}")
    public ResponseEntity<IngredientDTO> inc(@PathVariable Long id,
                                                     @RequestParam(name = "quality", defaultValue = "1") int quality) {
        Ingredient ing = ingredientService.increment(id, quality);
        return ResponseEntity.ok(IngredientDTO.fromIngredient(ing));
    }

    @Operation(summary = "Decrementar stock")
    @PostMapping(value = "/decrement/{id}")
    public ResponseEntity<IngredientDTO> dec(@PathVariable Long id,
                                                     @RequestParam(name = "quality", defaultValue = "1") int quality) {
        Ingredient ing = ingredientService.decrement(id, quality);
        return ResponseEntity.ok(IngredientDTO.fromIngredient(ing));
    }
}
