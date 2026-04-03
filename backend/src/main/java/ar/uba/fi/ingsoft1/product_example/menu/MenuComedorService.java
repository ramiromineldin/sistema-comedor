package ar.uba.fi.ingsoft1.product_example.menu;

import ar.uba.fi.ingsoft1.product_example.alimentos.Alimento;
import ar.uba.fi.ingsoft1.product_example.combos.ComboRepository;
import ar.uba.fi.ingsoft1.product_example.products.Product;
import ar.uba.fi.ingsoft1.product_example.products.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;

@Service
@Transactional
@RequiredArgsConstructor
public class MenuComedorService {

    private final MenuComedorRepository menuComedorRepository;
    private final ProductRepository productRepository;
    private final ComboRepository comboRepository;

    public MenuComedorDTO create(MenuComedorCreateDTO dto){
        MenuComedor menu = new MenuComedor(dto.nombre(), dto.descripcion(), dto.fecha());
        menuComedorRepository.save(menu);
        return MenuComedorDTO.fromMenuComedor(menu, comboRepository);
    }

    @Transactional(readOnly = true)
    public MenuComedorDTO getAdminById(Long id) {
        MenuComedor menu = menuComedorRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Menu no encontrado"));
        return MenuComedorDTO.fromMenuComedor(menu, comboRepository);
    }

    @Transactional(readOnly = true)
    public MenuComedorDTO getMenuHoy() {
        LocalDate hoy = LocalDate.now(ZoneId.of("America/Argentina/Buenos_Aires"));

        MenuComedor menu = menuComedorRepository.findFirstByActivoTrueAndFechaOrderByIdAsc(hoy).orElseThrow(() -> new IllegalArgumentException("No hay menú para hoy"));

        return MenuComedorDTO.fromMenuComedor(menu, comboRepository);
    }

    public void deactivate(Long id) {
        MenuComedor m = menuComedorRepository.findById(id).orElseThrow(() ->new IllegalArgumentException("Menu no encontrado"));
        m.setActivo(false);
    }

    public MenuComedorDTO addProduct(Long menuId, Long productId) {
        MenuComedor menu = menuComedorRepository.findById(menuId).orElseThrow(() -> new IllegalArgumentException("Menu no encontrado"));
        Product p = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Producto " + productId + " no existe"));
        boolean existeProducto = menu.getTodosLosAlimentos().stream()
                .filter(a -> a instanceof Product)
                .map(Alimento::getId)
                .anyMatch(id -> id.equals(productId));
        if (existeProducto) {
            throw new IllegalArgumentException("Ya existe este producto");
        }
        menu.addAlimento(p);
        return MenuComedorDTO.fromMenuComedor(menu, comboRepository);
    }

    public MenuComedorDTO removeProduct(Long menuId, Long productId) {
        MenuComedor m = menuComedorRepository.findById(menuId).orElseThrow(() -> new IllegalArgumentException("Menu no encontrado"));

        m.getTodosLosAlimentos().stream()
                .filter(a -> a instanceof Product)
                .map(a -> (Product) a)
                .filter(p -> p.getId().equals(productId))
                .findFirst()
                .ifPresent(m::removeAlimento);

        return MenuComedorDTO.fromMenuComedor(m, comboRepository);
    }
}
