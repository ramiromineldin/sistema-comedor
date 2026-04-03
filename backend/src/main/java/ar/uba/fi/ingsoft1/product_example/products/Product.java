package ar.uba.fi.ingsoft1.product_example.products;

import ar.uba.fi.ingsoft1.product_example.alimentos.Alimento;
import ar.uba.fi.ingsoft1.product_example.alimentos.Contenedor;
import ar.uba.fi.ingsoft1.product_example.alimentos.ElementoMenu;
import ar.uba.fi.ingsoft1.product_example.alimentos.TipoAlimento;
import ar.uba.fi.ingsoft1.product_example.ingredients.Ingredient;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@DiscriminatorValue("PRODUCT")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Product extends Alimento implements Contenedor {

        @ManyToMany(fetch = FetchType.LAZY)
        @JoinTable(name = "product_ingredient",
                joinColumns = @JoinColumn(name = "product_id"),
                inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
        private List<Alimento> ingredientes = new ArrayList<>();
        public Product(String name, String description, BigDecimal price) {
                super(name, description, price, 0);
        }

        public Product(String name, String description, BigDecimal price, Integer stock) {
                super(name, description, price, stock);
        }

        public Product(String name, String description, BigDecimal price, Integer stock, List<Alimento> ingredientes) {
                super(name, description, price, stock);
                this.ingredientes = ingredientes;
        }

        @Override
        public void addElemento(ElementoMenu elemento) {
                if (elemento instanceof Ingredient && !ingredientes.contains(elemento)) {
                        ingredientes.add((Alimento) elemento);
                }
        }

        @Override
        public void removeElemento(ElementoMenu elemento) {
                ingredientes.remove(elemento);
        }

        @JsonIgnore
        @Override
        public List<ElementoMenu> getElementos() {
                return ingredientes.stream()
                        .map(alimento -> (ElementoMenu) alimento)
                        .collect(Collectors.toList());
        }

        @Override
        public boolean isAvailable() {
                return hasStock();
        }

        @Override
        public boolean hasStock() {
                return getCurrentStock() > 0;
        }


        @Override
        public Integer getCurrentStock() {
                Integer stockProducto = super.getCurrentStock();
                
                if (stockProducto == null || stockProducto <= 0) {
                        return 0;
                }
                
                
                if (ingredientes == null || ingredientes.isEmpty()) {
                        return stockProducto;
                }
                
                
                Integer minIngrediente = ingredientes.stream()
                        .map(Alimento::getCurrentStock)
                        .filter(stock -> stock != null)
                        .min(Integer::compareTo)
                        .orElse(stockProducto);
                
                
                return Math.min(stockProducto, minIngrediente);
        }

        @JsonIgnore
        @Override
        public List<Ingredient> getAllIngredients() {
                if (ingredientes == null || ingredientes.isEmpty()) {
                        return new ArrayList<>();
                }
                
                return ingredientes.stream()
                        .filter(alimento -> alimento instanceof Ingredient)
                        .map(alimento -> (Ingredient) alimento)
                        .collect(Collectors.toList());
        }

        @Override
        public BigDecimal calculateTotalPrice() {
                return getPrice();
        }
        
        @Override
        public TipoAlimento getTipo() {
                return TipoAlimento.PRODUCT;
        }

        
        public void decreaseStockWithIngredients(Integer quantity) {
                if (quantity == null || quantity <= 0) {
                        return;
                }
                
                
                decrementStock(quantity);
                
                
                if (ingredientes != null && !ingredientes.isEmpty()) {
                        for (Alimento ingrediente : ingredientes) {
                                ingrediente.decrementStock(quantity);
                        }
                }
        }
}
