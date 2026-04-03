package ar.uba.fi.ingsoft1.product_example.ingredients;

import ar.uba.fi.ingsoft1.product_example.alimentos.Alimento;
import ar.uba.fi.ingsoft1.product_example.alimentos.TipoAlimento;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("INGREDIENT")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Ingredient extends Alimento {
    private static final Integer OUT_OF_STOCK = 0;
    
    public Ingredient(String name, String description, BigDecimal price, Integer stock) {
        super(name, description, price, stock);
    }

    public Ingredient(String name, String description, BigDecimal price) {
        super(name, description, price, OUT_OF_STOCK);
    }

    @Override
    public boolean isAvailable() {
        return hasStock();
    }
    
    @Override
    public TipoAlimento getTipo() {
        return TipoAlimento.INGREDIENT;
    }
}
