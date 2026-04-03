package ar.uba.fi.ingsoft1.product_example.products;

import ar.uba.fi.ingsoft1.product_example.ingredients.Ingredient;
import ar.uba.fi.ingsoft1.product_example.ingredients.IngredientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    private ProductRepository productRepository;
    private IngredientService ingredientService;
    private ProductService productService;

    @BeforeEach
    void setup() {
        productRepository = mock(ProductRepository.class);
        ingredientService = mock(IngredientService.class);
        productService = new ProductService(productRepository, ingredientService);
    }

    @Test
    @DisplayName("Create product successfully")
    void createProductSuccessfully() {
        ProductCreateDTO dto = new ProductCreateDTO(
                "Hamburguesa", "Con queso", BigDecimal.valueOf(1500), List.of(1L, 2L)
        );

        
        Ingredient ingredient1 = new Ingredient("Carne", "Carne de res", BigDecimal.valueOf(200), 15);
        ingredient1.setId(1L);
        Ingredient ingredient2 = new Ingredient("Pan", "Pan de hamburguesa", BigDecimal.valueOf(100), 20);
        ingredient2.setId(2L);

        when(ingredientService.getIngredient(1L)).thenReturn(ingredient1);
        when(ingredientService.getIngredient(2L)).thenReturn(ingredient2);

        Product saved = new Product("Hamburguesa", "Con queso", BigDecimal.valueOf(1500), 15);
        saved.setId(1L);

        when(productRepository.save(any(Product.class))).thenReturn(saved);

        ProductDTO result = productService.createProduct(dto);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Get existing product by ID")
    void getProductById() {
        Product product = new Product("Pizza", "Muzzarella", BigDecimal.valueOf(2000), 8);
        product.setId(5L);

        when(productRepository.findById(5L)).thenReturn(Optional.of(product));

        Optional<ProductDTO> found = productService.getProductById(5L);

        assertTrue(found.isPresent());
    }

    @Test
    @DisplayName("Get product by ID returns empty when not found")
    void productNotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<ProductDTO> found = productService.getProductById(99L);

        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Add ingredient to existing product")
    void addIngredientToProduct() {
        Product product = new Product("Empanada", "Carne", BigDecimal.valueOf(500), 10);
        product.setId(1L);

        Ingredient ingredient = new Ingredient("Cebolla", "Dorada", BigDecimal.valueOf(50), 5);
        ingredient.setId(2L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(ingredientService.getIngredient(2L)).thenReturn(ingredient);
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductDTO updated = productService.addIngredient(1L, 2L);

        assertNotNull(updated);
    }

    @Test
    @DisplayName("Throws exception when adding ingredient to nonexistent product")
    void addIngredientProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> productService.addIngredient(1L, 2L));
    }

    @Test
    @DisplayName("Update existing product successfully")
    void updateProductSuccessfully() {
        Product original = new Product("Sandwich", "Jamón y queso", BigDecimal.valueOf(700), 5);
        original.setId(3L);

        Product updated = new Product("Sandwich", "Jamón, queso y tomate", BigDecimal.valueOf(800), 5);
        updated.setId(3L);

        ProductUpdateDTO updateDTO = mock(ProductUpdateDTO.class);
        when(updateDTO.applyTo(original)).thenReturn(updated);
        when(productRepository.findById(3L)).thenReturn(Optional.of(original));
        when(productRepository.save(updated)).thenReturn(updated);

        Optional<ProductDTO> result = productService.updateProduct(3L, updateDTO);

        assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("Get all products returns all available products")
    void getAllProductsReturnsAll() {
        Product p1 = new Product("Hamburguesa", "Con queso", BigDecimal.valueOf(1500), 10);
        p1.setId(1L);
        Product p2 = new Product("Pizza", "Muzzarella", BigDecimal.valueOf(2000), 5);
        p2.setId(2L);

        when(productRepository.findAll()).thenReturn(List.of(p1, p2));

        List<ProductDTO> products = productService.getAllProducts();

        assertEquals(2, products.size());
        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("Delete product successfully")
    void deleteProductSuccessfully() {
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        productService.deleteProduct(1L);

        verify(productRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Throws exception when deleting nonexistent product")
    void deleteNonexistentProductThrows() {
        when(productRepository.existsById(999L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> productService.deleteProduct(999L));
    }

    @Test
    @DisplayName("Find product by ID returns existing product")
    void findByIdReturnsProduct() {
        Product product = new Product("Empanada", "Carne", BigDecimal.valueOf(500), 10);
        product.setId(7L);

        when(productRepository.findById(7L)).thenReturn(Optional.of(product));

        Product found = productService.findById(7L);

        assertNotNull(found);
        assertEquals(7L, found.getId());
    }

    @Test
    @DisplayName("Save product throws when product is null")
    void saveProductThrowsWhenNull() {
        assertThrows(IllegalArgumentException.class, () -> productService.save(null));
    }

    @Test
    @DisplayName("Save product saves successfully")
    void saveProductSuccessfully() {
        Product product = new Product("Empanada", "Carne", BigDecimal.valueOf(500), 10);
        product.setId(1L);

        when(productRepository.save(product)).thenReturn(product);

        Product saved = productService.save(product);

        assertNotNull(saved);
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("FindById throws when id is null")
    void findByIdThrowsWhenIdIsNull() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> productService.findById(null)
        );

        assertTrue(exception.getMessage().contains("ID del producto"));
    }

    @Test
    @DisplayName("Product decreaseStockWithIngredients decrements correctly")
    void productDecreaseStockWithIngredientsDecrementsCorrectly() {
        Product product = new Product("Hamburguesa", "Con queso", BigDecimal.valueOf(1500), 10);
        
        product.decreaseStockWithIngredients(3);
        
        assertEquals(7, product.getCurrentStock());
    }

    @Test
    @DisplayName("Product decreaseStockWithIngredients does nothing when quantity is null")
    void productDecreaseStockWithIngredientsDoesNothingWhenQuantityIsNull() {
        Product product = new Product("Hamburguesa", "Con queso", BigDecimal.valueOf(1500), 10);
        
        product.decreaseStockWithIngredients(null);
        
        assertEquals(10, product.getCurrentStock());
    }

    @Test
    @DisplayName("Product decreaseStockWithIngredients does nothing when quantity is zero")
    void productDecreaseStockWithIngredientsDoesNothingWhenQuantityIsZero() {
        Product product = new Product("Hamburguesa", "Con queso", BigDecimal.valueOf(1500), 10);
        
        product.decreaseStockWithIngredients(0);
        
        assertEquals(10, product.getCurrentStock());
    }

    @Test
    @DisplayName("Product removeElemento removes ingredient")
    void productRemoveElementoRemovesIngredient() {
        Product product = new Product("Hamburguesa", "Completa", BigDecimal.valueOf(1500), 10);
        Ingredient carne = new Ingredient("Carne", "500g", BigDecimal.valueOf(800), 20);
        carne.setId(1L);
        
        product.addElemento(carne);
        assertEquals(1, product.getIngredientes().size());
        
        product.removeElemento(carne);
        assertEquals(0, product.getIngredientes().size());
    }

    @Test
    @DisplayName("Product getElementos returns list of ingredients")
    void productGetElementosReturnsListOfIngredients() {
        Product product = new Product("Hamburguesa", "Completa", BigDecimal.valueOf(1500), 10);
        Ingredient carne = new Ingredient("Carne", "500g", BigDecimal.valueOf(800), 20);
        Ingredient pan = new Ingredient("Pan", "Integral", BigDecimal.valueOf(200), 15);
        
        product.addElemento(carne);
        product.addElemento(pan);
        
        assertEquals(2, product.getElementos().size());
    }

    @Test
    @DisplayName("Product isAvailable returns false when hasStock is false")
    void productIsAvailableReturnsFalseWhenHasStockIsFalse() {
        Product product = new Product("Hamburguesa", "Sin stock", BigDecimal.valueOf(1500), 0);
        Ingredient carne = new Ingredient("Carne", "500g", BigDecimal.valueOf(800), 20);
        
        product.addElemento(carne);
        
        assertFalse(product.isAvailable());
    }

    @Test
    @DisplayName("Product decreaseStockWithIngredients decrements ingredients stock")
    void productDecreaseStockWithIngredientsDecrementsIngredientsStock() {
        Product product = new Product("Hamburguesa", "Completa", BigDecimal.valueOf(1500), 10);
        Ingredient carne = new Ingredient("Carne", "500g", BigDecimal.valueOf(800), 20);
        Ingredient pan = new Ingredient("Pan", "Integral", BigDecimal.valueOf(200), 15);
        
        product.addElemento(carne);
        product.addElemento(pan);
        
        product.decreaseStockWithIngredients(3);
        
        assertEquals(7, product.getCurrentStock());
        assertEquals(17, carne.getCurrentStock());
        assertEquals(12, pan.getCurrentStock());
    }

    @Test
    @DisplayName("Product decreaseStockWithIngredients handles null ingredients list")
    void productDecreaseStockWithIngredientsHandlesNullIngredientsList() {
        Product product = new Product("Hamburguesa", "Sin ingredientes", BigDecimal.valueOf(1500), 10);
        
        product.decreaseStockWithIngredients(2);
        
        assertEquals(8, product.getCurrentStock());
    }

    @Test
    @DisplayName("Product decreaseStockWithIngredients handles empty ingredients list")
    void productDecreaseStockWithIngredientsHandlesEmptyIngredientsList() {
        Product product = new Product("Hamburguesa", "Sin ingredientes", BigDecimal.valueOf(1500), 10);
        product.setIngredientes(new java.util.ArrayList<>());
        
        product.decreaseStockWithIngredients(2);
        
        assertEquals(8, product.getCurrentStock());
    }
}
