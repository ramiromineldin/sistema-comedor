package ar.uba.fi.ingsoft1.product_example.combos;

import ar.uba.fi.ingsoft1.product_example.alimentos.Alimento;
import ar.uba.fi.ingsoft1.product_example.alimentos.Contenedor;
import ar.uba.fi.ingsoft1.product_example.alimentos.ElementoMenu;
import ar.uba.fi.ingsoft1.product_example.alimentos.TipoAlimento;
import ar.uba.fi.ingsoft1.product_example.ingredients.Ingredient;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@DiscriminatorValue("COMBO")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Combo extends Alimento implements Contenedor {

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
        name = "combo_alimentos",
        joinColumns = @JoinColumn(name = "combo_id"),
        inverseJoinColumns = @JoinColumn(name = "alimento_id")
    )
    private List<Alimento> productos = new ArrayList<>();
    private static final Integer OUT_OF_STOCK = 0;

    @Column
    private BigDecimal descuento = BigDecimal.ZERO; 

    public Combo(String name, String description, BigDecimal price) {
        super(name, description, price, OUT_OF_STOCK);
    }

    public Combo(String name, String description, BigDecimal price, Integer stock) {
        super(name, description, price, stock);
    }

    public Combo(String name, String description, BigDecimal price, Integer stock, BigDecimal descuento) {
        super(name, description, price, stock);
        this.descuento = descuento;
    }

    @Override
    public void addElemento(ElementoMenu elemento) {
        if (elemento instanceof Alimento && elemento != null && !productos.contains(elemento)) {
            productos.add((Alimento) elemento);
        }
    }

    @Override
    public void removeElemento(ElementoMenu elemento) {
        productos.remove(elemento);
    }

    @Override
    public List<ElementoMenu> getElementos() {
        return productos.stream()
                .map(alimento -> (ElementoMenu) alimento)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAvailable() {
        if (!hasStock()) {
            return false;
        }
        if (productos == null || productos.isEmpty()) {
            return false;
        }
        
        return productos.stream().allMatch(Alimento::isAvailable);
    }

    @Override
    public List<Ingredient> getAllIngredients() {
        Set<Ingredient> allIngredients = new HashSet<>();
        
        if (productos != null) {
            for (Alimento producto : productos) {
                if (producto instanceof Contenedor) {
                    allIngredients.addAll(((Contenedor) producto).getAllIngredients());
                }
            }
        }
        
        return new ArrayList<>(allIngredients);
    }

    @Override
    public BigDecimal calculateTotalPrice() {
        if (getPrice() != null && getPrice().compareTo(BigDecimal.ZERO) > 0) {
            return getPrice();
        }
        BigDecimal totalPrice = productos.stream()
            .filter(producto -> producto instanceof Contenedor)
            .map(producto -> ((Contenedor) producto).calculateTotalPrice())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (descuento != null && descuento.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal montoDescuento = totalPrice.multiply(descuento);
            totalPrice = totalPrice.subtract(montoDescuento);
        }
        
        return totalPrice.max(BigDecimal.ZERO); 
    }
    
    @Override
    public TipoAlimento getTipo() {
        return TipoAlimento.COMBO;
    }
}