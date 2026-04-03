package ar.uba.fi.ingsoft1.product_example.user;

import ar.uba.fi.ingsoft1.product_example.common.exception.InvalidPasswordException;
import ar.uba.fi.ingsoft1.product_example.common.exception.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminUserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDTO> getKitchenStaff() {
        return userRepository.findByRole(Role.PERSONAL_COCINA)
                .stream()
                .map(UserDTO::fromUser)
                .collect(Collectors.toList());
    }

    public void createKitchenStaff(UserCreateDTO data) {
        if (userRepository.findByUsername(data.username()).isPresent()) {
            throw new UserAlreadyExistsException(data.username());
        }
        if (!isValidPassword(data.password())) {
            throw new InvalidPasswordException();
        }

        UserCreateDTO kitchenStaffData = new UserCreateDTO(
                data.username(),
                data.password(),
                data.nombre(),
                data.apellido(),
                data.email(),
                data.edad(),
                data.genero(),
                data.domicilio(),
                Role.PERSONAL_COCINA
        );

        User newUser = kitchenStaffData.asUser(passwordEncoder::encode);
        newUser.setEmailVerified(true);
        userRepository.save(newUser);
    }

    public void updateKitchenStaff(Long id, UserUpdateDTO data) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (user.getRole() != Role.PERSONAL_COCINA) {
            throw new RuntimeException("Solo se pueden editar usuarios de cocina");
        }

        user.setNombre(data.nombre());
        user.setApellido(data.apellido());
        user.setEmail(data.email());
        user.setEdad(data.edad());
        user.setGenero(data.genero());
        user.setDomicilio(data.domicilio());
        
        userRepository.save(user);
    }

    public void deleteKitchenStaff(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (user.getRole() != Role.PERSONAL_COCINA) {
            throw new RuntimeException("Solo se pueden eliminar usuarios de cocina");
        }

        userRepository.delete(user);
    }

    public UserDTO getKitchenStaffById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (user.getRole() != Role.PERSONAL_COCINA) {
            throw new RuntimeException("Usuario no es personal de cocina");
        }

        return UserDTO.fromUser(user);
    }

    private boolean isValidPassword(String password) {
        return password != null &&
                password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*\\d.*");
    }
}