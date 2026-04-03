package ar.uba.fi.ingsoft1.product_example.alimentos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_alimento", discriminatorType = DiscriminatorType.STRING)
@Data
@NoArgsConstructor
public abstract class Alimento implements ElementoMenu, AlimentoSimple {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer currentStock = 0;
    private static final Integer OUT_OF_STOCK = 0;

    public Alimento(String name, String description, BigDecimal price, Integer stock) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.currentStock = stock;
    }

    @Override
    public abstract boolean isAvailable();
    
    public abstract TipoAlimento getTipo();

    @Override
    public boolean hasStock() {
        return currentStock != null && currentStock > OUT_OF_STOCK;
    }

    @Override
    public void incrementStock(Integer quantity) {
        if (quantity != null && quantity > OUT_OF_STOCK) {
            this.currentStock = (this.currentStock == null ? OUT_OF_STOCK : this.currentStock) + quantity;
        }
    }

    @Override
    public void decrementStock(Integer quantity) {
        if (quantity != null && quantity > OUT_OF_STOCK && this.currentStock != null) {
            this.currentStock = Math.max(OUT_OF_STOCK, this.currentStock - quantity);
        }
    }

    @Override
    public void setStock(Integer stock) {
        this.currentStock = stock != null ? Math.max(OUT_OF_STOCK, stock) : OUT_OF_STOCK;
    }
}