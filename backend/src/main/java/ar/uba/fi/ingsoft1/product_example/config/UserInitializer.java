package ar.uba.fi.ingsoft1.product_example.config;

import ar.uba.fi.ingsoft1.product_example.user.Role;
import ar.uba.fi.ingsoft1.product_example.user.User;
import ar.uba.fi.ingsoft1.product_example.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserInitializer {

    @Bean
    public CommandLineRunner initUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("estudiante").isEmpty()) {
                User user = new User(
                        "estudiante",
                        passwordEncoder.encode("estudiante"),
                        "Juan",
                        "Pérez",
                        "estudiante@fi.uba.ar",
                        22,
                        "M",
                        "Ciudad Universitaria - Pabellón II",
                        Role.USER
                );
                userRepository.save(user);
                System.out.println("✅ Usuario USER creado por defecto (usuario: estudiante / pass: estudiante)");
            } else {
                System.out.println("ℹ️ Usuario USER ya existe, no se crea otro.");
            }
        };
    }
}