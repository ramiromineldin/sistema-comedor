package ar.uba.fi.ingsoft1.product_example.user;

public record UserDTO(
        Long id,
        String username,
        String nombre,
        String apellido,
        String email,
        Integer edad,
        String genero,
        String domicilio,
        Role role,
        boolean emailVerified
) {
    public static UserDTO fromUser(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getNombre(),
                user.getApellido(),
                user.getEmail(),
                user.getEdad(),
                user.getGenero(),
                user.getDomicilio(),
                user.getRole(),
                user.isEmailVerified()
        );
    }
}