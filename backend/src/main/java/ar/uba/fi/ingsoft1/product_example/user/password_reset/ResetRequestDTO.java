package ar.uba.fi.ingsoft1.product_example.user.password_reset;

import jakarta.validation.constraints.NotBlank;

public record ResetRequestDTO (
        @NotBlank String token,
        @NotBlank String newPassword)
{ }
