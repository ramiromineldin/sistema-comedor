package ar.uba.fi.ingsoft1.product_example.combos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComboCreateDTO {
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 1, max = 100, message = "El nombre debe tener entre 1 y 100 caracteres")
    private String name;
    
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String description;
    
    @NotNull(message = "El descuento es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El descuento debe ser mayor o igual a 0")
    @DecimalMax(value = "1.0", inclusive = true, message = "El descuento debe ser menor o igual a 1")
    private BigDecimal discount;

    @NotEmpty(message = "El combo debe tener al menos dos productos")
    @Size(min = 2, message = "El combo debe tener al menos dos productos")
    private List<Long> alimentoIds; 
}