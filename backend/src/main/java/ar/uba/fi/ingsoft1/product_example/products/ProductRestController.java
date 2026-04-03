package ar.uba.fi.ingsoft1.product_example.products;

import ar.uba.fi.ingsoft1.product_example.ingredients.AddIngredientDTO;
import ar.uba.fi.ingsoft1.product_example.ingredients.IngredientDTO;
import ar.uba.fi.ingsoft1.product_example.ingredients.IngredientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
@Validated
@RequiredArgsConstructor
@Tag(name = "Products")
class ProductRestController {
    private final ProductService productService;
    private final IngredientService ingredientService;

    @Operation(summary = "Crear nuevo producto")
    @PostMapping()
    public ResponseEntity<ProductDTO> create(@Valid @RequestBody ProductCreateDTO dto) {
        ProductDTO product = productService.createProduct(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @Operation(summary = "Obtener un producto por id")
    @GetMapping(value = "/{id}")
    public ResponseEntity<ProductDTO> get(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(dto -> ResponseEntity.status(HttpStatus.OK).body(dto))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Obtener todos los productos")
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Agregar ingrediente existente al producto")
    @PostMapping(value = "/add/{id}")
    public ResponseEntity<ProductDTO> addIngredient(@PathVariable Long id,
                                                    @Valid @RequestBody AddIngredientDTO dto) {
        try {
            var updated = productService.addIngredient(id, dto.ingredientId());
            return ResponseEntity.status(HttpStatus.OK).body(updated);
        } catch (IllegalArgumentException e){
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Eliminar un producto")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
