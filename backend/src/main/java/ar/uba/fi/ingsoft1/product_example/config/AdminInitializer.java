package ar.uba.fi.ingsoft1.product_example.config;

import ar.uba.fi.ingsoft1.product_example.user.Role;
import ar.uba.fi.ingsoft1.product_example.user.User;
import ar.uba.fi.ingsoft1.product_example.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    @Bean
    public CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User(
                        "admin",
                        passwordEncoder.encode("admin"),
                        "admin",
                        "admin",
                                "admin@admin.com",
                                30,
                        "admin",
                        "admin 123",
                        Role.ADMIN
                );
                userRepository.save(admin);
                System.out.println("✅ Usuario ADMIN creado por defecto (usuario: admin / pass: admin)");
            } else {
                System.out.println("ℹ️ Usuario ADMIN ya existe, no se crea otro.");
            }
        };
    }
}
