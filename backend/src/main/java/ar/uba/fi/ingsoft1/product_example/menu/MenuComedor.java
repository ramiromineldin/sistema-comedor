package ar.uba.fi.ingsoft1.product_example.menu;

import ar.uba.fi.ingsoft1.product_example.alimentos.Alimento;
import ar.uba.fi.ingsoft1.product_example.combos.Combo;
import ar.uba.fi.ingsoft1.product_example.products.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "menu_comedor")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuComedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column
    private String descripcion;

    @Column(nullable = false, unique = true)
    private LocalDate fecha;

    @Column(nullable = false)
    private Boolean activo = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "menu_alimentos",
        joinColumns = @JoinColumn(name = "menu_id"),
        inverseJoinColumns = @JoinColumn(name = "alimento_id")
    )
    private List<Alimento> alimentos = new ArrayList<>();

    public MenuComedor(String nombre, String descripcion, LocalDate fecha) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.activo = true;
    }

    public List<Product> getProductosDisponibles() {
        return alimentos.stream()
                .filter(alimento -> alimento instanceof Product)
                .map(alimento -> (Product) alimento)
                .filter(Product::isAvailable)
                .collect(Collectors.toList());
    }

    public List<Combo> getCombosDisponibles() {
        return alimentos.stream()
                .filter(alimento -> alimento instanceof Combo)
                .map(alimento -> (Combo) alimento)
                .filter(Combo::isAvailable)
                .collect(Collectors.toList());
    }

    public List<Alimento> getTodosLosAlimentosDisponibles() {
        return alimentos.stream()
                .filter(Alimento::isAvailable)
                .collect(Collectors.toList());
    }

    public List<Alimento> getTodosLosAlimentos() {
        return new ArrayList<>(alimentos);
    }

    public void addAlimento(Alimento alimento) {
        if (alimento != null && !alimentos.contains(alimento)) {
            alimentos.add(alimento);
        }
    }

    public void removeAlimento(Alimento alimento) {
        alimentos.remove(alimento);
    }

    public void clearAlimentos() {
        alimentos.clear();
    }

    public boolean hasAlimentosDisponibles() {
        return alimentos.stream().anyMatch(Alimento::isAvailable);
    }

    public int getCantidadProductosDisponibles() {
        return (int) alimentos.stream()
                .filter(alimento -> alimento instanceof Product)
                .filter(Alimento::isAvailable)
                .count();
    }

    public int getCantidadCombosDisponibles() {
        return (int) alimentos.stream()
                .filter(alimento -> alimento instanceof Combo)
                .filter(Alimento::isAvailable)
                .count();
    }
}