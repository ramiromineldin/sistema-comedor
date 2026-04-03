package ar.uba.fi.ingsoft1.product_example.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity(name = "users")
@NoArgsConstructor
public class User implements UserDetails, UserCredentials {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Setter
    @Column(nullable = false)
    private String password;

    @Getter
    @Setter
    @Column(nullable = false)
    private String nombre;

    @Getter
    @Setter
    @Column(nullable = false)
    private String apellido;

    @Getter
    @Setter
    @Column(unique = true,nullable = false)
    private String email;

    @Getter
    @Setter
    @Column(nullable = false)
    private Integer edad;

    @Getter
    @Setter 
    @Column(nullable = false)
    private String genero;

    @Setter
    @Getter
    @Column(nullable = false)
    private String domicilio;

    @Getter
    @Setter
    private boolean emailVerified;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    
    public User(String username, String password, String nombre, String apellido, String email, Integer edad, String genero, String domicilio, Role role) {
        this.username = username;
        this.password = password;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.edad = edad;
        this.genero = genero;
        this.domicilio = domicilio;
        this.role = role;
        this.emailVerified = false;
    }

    @Override
    public String username() {
        return this.username;
    }

    @Override
    public String password() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public Role expectedRole() {
        return this.role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }
    
}
