package ar.uba.fi.ingsoft1.product_example.products;

import ar.uba.fi.ingsoft1.product_example.ingredients.Ingredient;
import ar.uba.fi.ingsoft1.product_example.ingredients.IngredientDTO;
import ar.uba.fi.ingsoft1.product_example.ingredients.IngredientRepository;
import ar.uba.fi.ingsoft1.product_example.ingredients.IngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final IngredientService ingredientService;

    public Optional<ProductDTO> getProductById(long id) {
        return productRepository.findById(id).map(ProductDTO::new);
    }

    public ProductDTO createProduct(ProductCreateDTO data) {
        var product = data.asProduct();

        // Agregar ingredientes y calcular stock
        if (data.ingredientesIds() != null && !data.ingredientesIds().isEmpty()) {
            Integer minStock = Integer.MAX_VALUE;

            for (Long ingredientId : data.ingredientesIds()) {
                Ingredient ingredient = ingredientService.getIngredient(ingredientId);
                product.addElemento(ingredient);

                // Encontrar el stock mínimo entre todos los ingredientes
                Integer ingredientStock = ingredient.getCurrentStock();
                if (ingredientStock != null && ingredientStock < minStock) {
                    minStock = ingredientStock;
                }
            }

            // Establecer el stock del producto basándose en el ingrediente limitante
            product.setStock(minStock == Integer.MAX_VALUE ? 0 : minStock);
        } else {
            // Si no tiene ingredientes, stock = 0
            product.setStock(0);        }

        return new ProductDTO(productRepository.save(product));
    }

    public ProductDTO addIngredient(Long productId, Long ingredientId) {
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product %d no encontrado".formatted(productId)));

        Ingredient ingredient = ingredientService.getIngredient(ingredientId);
        product.addElemento(ingredient);

        var saved = productRepository.save(product);
        return new ProductDTO(saved);
    }

    public Optional<ProductDTO> updateProduct(Long id, ProductUpdateDTO update) {
        return productRepository.findById(id)
                .map(update::applyTo)
                .map(productRepository::save)
                .map(ProductDTO::new);
    }

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
            .map(ProductDTO::new)
            .toList();
    }

    public Product findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del producto es obligatorio");
        }
        return productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));
    }

    public Product save(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("El producto es obligatorio");
        }
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Prodcuto no encontrado");
        }
        productRepository.deleteById(id);
    }
}
