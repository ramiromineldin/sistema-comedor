package ar.uba.fi.ingsoft1.product_example.user.password_reset;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotRequestDTO (
        @NotBlank @Email String email
)
{ }
