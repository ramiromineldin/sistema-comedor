package ar.uba.fi.ingsoft1.product_example.menu;

import ar.uba.fi.ingsoft1.product_example.alimentos.Alimento;
import ar.uba.fi.ingsoft1.product_example.combos.Combo;
import ar.uba.fi.ingsoft1.product_example.combos.ComboRepository;
import ar.uba.fi.ingsoft1.product_example.products.Product;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public record MenuComedorDTO (
        Long id,
        String nombre,
        String descripcion,
        LocalDate fecha,
        Boolean activo,
        List<Product> alimentos,
        List<Combo> combos
)  {
    public static MenuComedorDTO fromMenuComedor(MenuComedor menuComedor, ComboRepository comboRepository){
        // Obtener todos los combos disponibles del sistema
        List<Combo> todosLosCombos = comboRepository.findAll().stream()
                .filter(Combo::isAvailable)
                .collect(Collectors.toList());
        
        return new MenuComedorDTO(
                menuComedor.getId(),
                menuComedor.getNombre(),
                menuComedor.getDescripcion(),
                menuComedor.getFecha(),
                menuComedor.getActivo(),
                menuComedor.getProductosDisponibles(),
                todosLosCombos
        );
    }
}