package ar.uba.fi.ingsoft1.product_example.combos;

import ar.uba.fi.ingsoft1.product_example.alimentos.Alimento;
import ar.uba.fi.ingsoft1.product_example.products.Product;
import ar.uba.fi.ingsoft1.product_example.products.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ComboService {

    private final ComboRepository comboRepository;
    private final ProductRepository productRepository;

    public ComboDTO createCombo(ComboCreateDTO dto) {


        if (dto.getAlimentoIds() == null || dto.getAlimentoIds().size() < 2) {
            throw new IllegalArgumentException("Un combo debe tener al menos dos productos");
        }
        Combo combo = new Combo(dto.getName(), dto.getDescription(), BigDecimal.ZERO);
        combo.setDescuento(dto.getDiscount());


        if (dto.getAlimentoIds() != null && !dto.getAlimentoIds().isEmpty()) {
            for (Long alimentoId : dto.getAlimentoIds()) {
                Product product = productRepository.findById(alimentoId)
                        .orElseThrow(() -> new IllegalArgumentException("Producto " + alimentoId + " no encontrado"));
                combo.addElemento(product);
            }
        }


        BigDecimal precioTotal = combo.calculateTotalPrice();
        combo.setPrice(precioTotal);

        Integer minStock = combo.getProductos().stream()
                .map(Alimento::getCurrentStock)
                .min(Integer::compareTo)
                .orElse(0);
        combo.setStock(minStock);


        comboRepository.save(combo);
        return new ComboDTO(combo);
    }

    @Transactional(readOnly = true)
    public Optional<ComboDTO> getComboById(Long id) {
        return comboRepository.findById(id)
                .map(ComboDTO::new);
    }

    @Transactional(readOnly = true)
    public List<ComboDTO> getAllCombos() {
        return comboRepository.findAll().stream()
                .map(ComboDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ComboDTO> searchCombosByName(String name) {
        return comboRepository.findByNameContaining(name).stream()
                .map(ComboDTO::new)
                .collect(Collectors.toList());
    }

    public ComboDTO addProductToCombo(Long comboId, Long productId) {
        Combo combo = comboRepository.findById(comboId)
                .orElseThrow(() -> new IllegalArgumentException("Combo no encontrado"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        combo.addElemento(product);

        combo.setPrice(null);
        BigDecimal precioTotal = combo.calculateTotalPrice();
        combo.setPrice(precioTotal);

        comboRepository.save(combo);
        return new ComboDTO(combo);
    }

    public ComboDTO removeProductFromCombo(Long comboId, Long productId) {
        Combo combo = comboRepository.findById(comboId)
                .orElseThrow(() -> new IllegalArgumentException("Combo no encontrado"));

        if (combo.getProductos().size() == 2) {
            throw new IllegalArgumentException("No se puede eliminar el producto. El combo debe tener al menos 2 productos. Si desea eliminarlo, borre el combo completo.");
        }

        Alimento alimento = combo.getProductos().stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado en el combo"));

        combo.removeElemento(alimento);

        combo.setPrice(null);
        BigDecimal precioTotal = combo.calculateTotalPrice();
        combo.setPrice(precioTotal);

        comboRepository.save(combo);
        return new ComboDTO(combo);
    }

    public void deleteCombo(Long id) {
        if (!comboRepository.existsById(id)) {
            throw new IllegalArgumentException("Combo no encontrado");
        }
        comboRepository.deleteById(id);
    }

    public ComboDTO updateComboDiscount(Long id, BigDecimal newDiscount) {
        Combo combo = comboRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Combo no encontrado"));

        combo.setDescuento(newDiscount);

        combo.setPrice(null);
        BigDecimal precioTotal = combo.calculateTotalPrice();
        combo.setPrice(precioTotal);

        comboRepository.save(combo);
        return new ComboDTO(combo);
    }
}