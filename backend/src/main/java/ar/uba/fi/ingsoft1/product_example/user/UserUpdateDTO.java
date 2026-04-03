package ar.uba.fi.ingsoft1.product_example.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserUpdateDTO(
        @NotBlank String nombre,
        @NotBlank String apellido,
        @NotBlank String email,
        @NotNull Integer edad,
        @NotBlank String genero,
        @NotBlank String domicilio
) {}