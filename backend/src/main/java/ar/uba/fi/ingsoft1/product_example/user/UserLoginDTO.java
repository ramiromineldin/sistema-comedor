package ar.uba.fi.ingsoft1.product_example.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserLoginDTO(
        @NotBlank String username,
        @NotBlank String password,
        @NotNull Role expectedRole
) implements UserCredentials {}
