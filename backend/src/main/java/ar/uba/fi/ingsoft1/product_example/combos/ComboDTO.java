package ar.uba.fi.ingsoft1.product_example.combos;

import ar.uba.fi.ingsoft1.product_example.alimentos.TipoAlimento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComboDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discount;
    private boolean available;
    private TipoAlimento tipo = TipoAlimento.COMBO;
    private List<SubAlimentoDTO> subAlimentos;
    
    public ComboDTO(Combo combo) {
        this.id = combo.getId();
        this.name = combo.getName();
        this.description = combo.getDescription();
        this.price = combo.getPrice();
        this.discount = combo.getDescuento();
        this.available = combo.isAvailable();
        this.subAlimentos = combo.getProductos().stream()
                .map(alimento -> new SubAlimentoDTO(
                        alimento.getId(), 
                        alimento.getName(), 
                        alimento.getTipo(),
                        alimento instanceof ar.uba.fi.ingsoft1.product_example.products.Product ? 
                            ((ar.uba.fi.ingsoft1.product_example.products.Product) alimento).getPrice() :
                            alimento instanceof Combo ?
                                ((Combo) alimento).getPrice() : null
                ))
                .collect(Collectors.toList());
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubAlimentoDTO {
        private Long id;
        private String name;
        private TipoAlimento tipo;
        private BigDecimal price;
    }
}