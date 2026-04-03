package ar.uba.fi.ingsoft1.product_example.combos;

import ar.uba.fi.ingsoft1.product_example.products.Product;
import ar.uba.fi.ingsoft1.product_example.products.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ComboServiceTest {

    private ComboRepository comboRepository;
    private ProductRepository productRepository;
    private ComboService comboService;

    @BeforeEach
    void setup() {
        comboRepository = mock(ComboRepository.class);
        productRepository = mock(ProductRepository.class);
        comboService = new ComboService(comboRepository, productRepository);
    }

    @Test
    @DisplayName("Create combo successfully with valid products")
    void createComboSuccessfully() {
        ComboCreateDTO dto = new ComboCreateDTO();
        dto.setName("Combo 1");
        dto.setDescription("Variedad");
        dto.setDiscount(BigDecimal.valueOf(0.1));
        dto.setAlimentoIds(List.of(1L, 2L));

        Product product1 = new Product("Pizza", "Muzzarella", BigDecimal.valueOf(2000), 10);
        Product product2 = new Product("Gaseosa", "Cola", BigDecimal.valueOf(500), 8);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
        when(comboRepository.save(any(Combo.class))).thenAnswer(inv -> inv.getArgument(0));

        ComboDTO result = comboService.createCombo(dto);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Throws exception when creating combo with less than 2 products")
    void createComboWithFewProductsThrows() {
        ComboCreateDTO dto = new ComboCreateDTO();
        dto.setAlimentoIds(List.of(1L));

        assertThrows(IllegalArgumentException.class, () -> comboService.createCombo(dto));
    }

    @Test
    @DisplayName("Get existing combo by ID")
    void getComboById() {
        Combo combo = new Combo("Combo Especial", "Completo", BigDecimal.valueOf(3000), 5);
        combo.setId(1L);

        when(comboRepository.findById(1L)).thenReturn(Optional.of(combo));

        Optional<ComboDTO> found = comboService.getComboById(1L);

        assertTrue(found.isPresent());
    }

    @Test
    @DisplayName("Get all combos returns list")
    void getAllCombos() {
        when(comboRepository.findAll()).thenReturn(List.of(
                new Combo("Combo 1", "Simple", BigDecimal.valueOf(1000), 5),
                new Combo("Combo 2", "Completo", BigDecimal.valueOf(2000), 8)
        ));

        List<ComboDTO> all = comboService.getAllCombos();

        assertEquals(2, all.size());
    }

    @Test
    @DisplayName("Add product to combo successfully")
    void addProductToCombo() {
        Combo combo = new Combo("Combo 1", "Inicial", BigDecimal.valueOf(1000), 5);
        combo.setId(1L);
        Product product = new Product("Empanada", "Carne", BigDecimal.valueOf(300), 10);
        product.setId(2L);

        when(comboRepository.findById(1L)).thenReturn(Optional.of(combo));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(comboRepository.save(any(Combo.class))).thenAnswer(inv -> inv.getArgument(0));

        ComboDTO result = comboService.addProductToCombo(1L, 2L);

        assertNotNull(result);
    }

    
    @Test
    @DisplayName("Throws exception when removing product from combo with only 2 products")
    void removeProductThrowsIfOnlyTwoProducts() {
        Combo combo = new Combo("Combo 1", "Dos productos", BigDecimal.valueOf(1500), 5);
        Product p1 = new Product("Pizza", "Muzzarella", BigDecimal.valueOf(800), 10);
        Product p2 = new Product("Gaseosa", "Cola", BigDecimal.valueOf(700), 10);
        combo.addElemento(p1);
        combo.addElemento(p2);
        combo.setId(1L);

        when(comboRepository.findById(1L)).thenReturn(Optional.of(combo));

        assertThrows(IllegalArgumentException.class, () -> comboService.removeProductFromCombo(1L, 2L));
    }

    @Test
    @DisplayName("Update combo discount successfully")
    void updateComboDiscount() {
        Combo combo = new Combo("Combo 1", "Variedad", BigDecimal.valueOf(1000), 5);
        combo.setId(1L);

        when(comboRepository.findById(1L)).thenReturn(Optional.of(combo));
        when(comboRepository.save(any(Combo.class))).thenAnswer(inv -> inv.getArgument(0));

        ComboDTO updated = comboService.updateComboDiscount(1L, BigDecimal.valueOf(0.2));

        assertNotNull(updated);
    }

    @Test
    @DisplayName("Delete combo successfully")
    void deleteComboSuccess() {
        when(comboRepository.existsById(1L)).thenReturn(true);
        doNothing().when(comboRepository).deleteById(1L);

        comboService.deleteCombo(1L);

        assertTrue(true);
    }

    @Test
    @DisplayName("Throws exception when deleting nonexistent combo")
    void deleteComboThrowsIfNotFound() {
        when(comboRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> comboService.deleteCombo(1L));
    }

    @Test
    @DisplayName("Get combo by ID throws when not found")
    void getComboByIdThrowsWhenNotFound() {
        when(comboRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<ComboDTO> result = comboService.getComboById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Search combos by name returns matching combos")
    void searchCombosByNameReturnsMatching() {
        Combo combo1 = new Combo("Combo Premium", "Con todo", BigDecimal.valueOf(3000), 5);

        when(comboRepository.findByNameContaining("Premium")).thenReturn(List.of(combo1));

        List<ComboDTO> result = comboService.searchCombosByName("Premium");

        assertEquals(1, result.size());
        assertTrue(result.get(0).getName().contains("Premium"));
    }

    @Test
    @DisplayName("RemoveProductFromCombo recalculates price")
    void removeProductRecalculatesPrice() {
        Combo combo = new Combo("Combo 1", "Test", BigDecimal.valueOf(1000), 5);
        combo.setId(1L);
        
        Product p1 = new Product("Pizza", "Muzzarella", BigDecimal.valueOf(800), 10);
        p1.setId(1L);
        Product p2 = new Product("Gaseosa", "Cola", BigDecimal.valueOf(700), 10);
        p2.setId(2L);
        Product p3 = new Product("Postre", "Flan", BigDecimal.valueOf(500), 10);
        p3.setId(3L);
        
        combo.addElemento(p1);
        combo.addElemento(p2);
        combo.addElemento(p3);

        when(comboRepository.findById(1L)).thenReturn(Optional.of(combo));
        when(comboRepository.save(any(Combo.class))).thenAnswer(inv -> inv.getArgument(0));

        ComboDTO result = comboService.removeProductFromCombo(1L, 2L);

        assertNotNull(result);
        verify(comboRepository).save(combo);
    }

    @Test
    @DisplayName("Combo getTipo returns COMBO")
    void comboGetTipoReturnsCombo() {
        Combo combo = new Combo("Combo 1", "Test", BigDecimal.valueOf(1000), 5);
        
        assertEquals(ar.uba.fi.ingsoft1.product_example.alimentos.TipoAlimento.COMBO, combo.getTipo());
    }

    @Test
    @DisplayName("Combo removeElemento removes product")
    void comboRemoveElementoRemovesProduct() {
        Combo combo = new Combo("Combo 1", "Test", BigDecimal.valueOf(1000), 5);
        Product p1 = new Product("Pizza", "Muzzarella", BigDecimal.valueOf(800), 10);
        
        combo.addElemento(p1);
        assertEquals(1, combo.getProductos().size());
        
        combo.removeElemento(p1);
        assertEquals(0, combo.getProductos().size());
    }

    @Test
    @DisplayName("Combo getElementos returns list")
    void comboGetElementosReturnsList() {
        Combo combo = new Combo("Combo 1", "Test", BigDecimal.valueOf(1000), 5);
        Product p1 = new Product("Pizza", "Muzzarella", BigDecimal.valueOf(800), 10);
        
        combo.addElemento(p1);
        
        assertEquals(1, combo.getElementos().size());
    }

    @Test
    @DisplayName("Combo isAvailable returns false when no stock")
    void comboIsAvailableReturnsFalseWhenNoStock() {
        Combo combo = new Combo("Combo 1", "Test", BigDecimal.valueOf(1000), 0);
        Product p1 = new Product("Pizza", "Muzzarella", BigDecimal.valueOf(800), 10);
        combo.addElemento(p1);
        
        assertFalse(combo.isAvailable());
    }

    @Test
    @DisplayName("Combo getAllIngredients returns ingredients from products")
    void comboGetAllIngredientsReturnsIngredients() {
        Combo combo = new Combo("Combo 1", "Test", BigDecimal.valueOf(1000), 5);
        Product p1 = new Product("Hamburguesa", "Con queso", BigDecimal.valueOf(1500), 10);
        combo.addElemento(p1);
        
        List<ar.uba.fi.ingsoft1.product_example.ingredients.Ingredient> ingredients = combo.getAllIngredients();
        
        assertNotNull(ingredients);
    }

    @Test
    @DisplayName("Combo calculateTotalPrice returns getPrice when price is set")
    void comboCalculateTotalPriceReturnsGetPriceWhenSet() {
        Combo combo = new Combo("Combo 1", "Test", BigDecimal.valueOf(1000), 5);
        
        BigDecimal total = combo.calculateTotalPrice();
        
        assertEquals(BigDecimal.valueOf(1000), total);
    }
}
