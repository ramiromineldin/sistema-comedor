package ar.uba.fi.ingsoft1.product_example.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.function.Function;

public record UserCreateDTO(
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String nombre,
        @NotBlank String apellido,
        @NotBlank @Email String email,
        @NotNull Integer edad,
        @NotBlank String genero,
        @NotBlank String domicilio,
        @NotNull Role role
) {
    public User asUser(Function<String, String> encryptPassword) {
        return new User(username, encryptPassword.apply(password), nombre, apellido, email, edad, genero, domicilio, role);
    }
}
