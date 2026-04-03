package ar.uba.fi.ingsoft1.product_example.config;

import ar.uba.fi.ingsoft1.product_example.user.Role;
import ar.uba.fi.ingsoft1.product_example.user.User;
import ar.uba.fi.ingsoft1.product_example.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class CocinaInitializer {

    @Bean
    public CommandLineRunner initCocina(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("cocinero").isEmpty()) {
                User cocina = new User(
                        "cocinero",
                        passwordEncoder.encode("cocina"),
                        "Personal de",
                        "Cocina",
                        "cocinero@comedor.com",
                        25,
                        "M",
                        "Cocina - Comedor Universitario",
                        Role.PERSONAL_COCINA
                );
                userRepository.save(cocina);
                System.out.println("✅ Usuario PERSONAL_COCINA creado por defecto (usuario: cocinero / pass: cocina)");
            } else {
                System.out.println("ℹ️ Usuario PERSONAL_COCINA ya existe, no se crea otro.");
            }
        };
    }
}