package ar.uba.fi.ingsoft1.product_example.menu;


import lombok.NonNull;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

public record MenuComedorCreateDTO (
        @Validated @NonNull String nombre,
        @Validated @NonNull String descripcion,
        @Validated @NonNull LocalDate fecha
    ) {}