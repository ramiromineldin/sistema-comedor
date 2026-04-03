package ar.uba.fi.ingsoft1.product_example.alimentos;

import ar.uba.fi.ingsoft1.product_example.ingredients.Ingredient;
import java.math.BigDecimal;
import java.util.List;


public interface Contenedor extends ElementoMenu {
    void addElemento(ElementoMenu elemento);
    void removeElemento(ElementoMenu elemento);
    List<ElementoMenu> getElementos();
    List<Ingredient> getAllIngredients();
    BigDecimal calculateTotalPrice();
}