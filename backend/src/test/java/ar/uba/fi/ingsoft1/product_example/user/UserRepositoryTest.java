package ar.uba.fi.ingsoft1.product_example.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "password123";
    private static final String NOMBRE = "Juan";
    private static final String APELLIDO = "Perez";
    private static final String EMAIL = "juan@test.com";
    private static final Integer EDAD = 25;
    private static final String GENERO = "MASCULINO";
    private static final String DOMICILIO = "Calle 123";

    @Test
    @DisplayName("Find by username returns user when exists")
    void findByUsernameExists() {
        User user = new User(USERNAME, PASSWORD, NOMBRE, APELLIDO, EMAIL, EDAD, GENERO, DOMICILIO, Role.USER);
        entityManager.persistAndFlush(user);

        Optional<User> result = userRepository.findByUsername(USERNAME);

        assertTrue(result.isPresent());
        assertEquals(USERNAME, result.get().getUsername());
        assertEquals(EMAIL, result.get().getEmail());
        assertEquals(NOMBRE, result.get().getNombre());
        assertEquals(APELLIDO, result.get().getApellido());
        assertEquals(Role.USER, result.get().getRole());
        assertFalse(result.get().isEmailVerified()); // Por defecto debe ser false
    }

    @Test
    @DisplayName("Find by username returns empty when not exists")
    void findByUsernameNotExists() {
        Optional<User> result = userRepository.findByUsername("nonexistent");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Save user creates new user successfully")
    void saveUserSuccess() {
        User user = new User(USERNAME, PASSWORD, NOMBRE, APELLIDO, EMAIL, EDAD, GENERO, DOMICILIO, Role.USER);

        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());
        assertEquals(USERNAME, savedUser.getUsername());
        assertEquals(EMAIL, savedUser.getEmail());
        assertFalse(savedUser.isEmailVerified());
        assertEquals(Role.USER, savedUser.getRole());
    }

    @Test
    @DisplayName("Find all returns all users")
    void findAllReturnsAllUsers() {
        User user1 = new User("user1", PASSWORD, NOMBRE, APELLIDO, "user1@test.com", EDAD, GENERO, DOMICILIO, Role.USER);
        User user2 = new User("user2", PASSWORD, "Maria", "Garcia", "user2@test.com", 30, "FEMENINO", "Calle 456", Role.ADMIN);

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();

        List<User> allUsers = userRepository.findAll();

        assertEquals(2, allUsers.size());
        assertTrue(allUsers.stream().anyMatch(u -> u.getUsername().equals("user1")));
        assertTrue(allUsers.stream().anyMatch(u -> u.getUsername().equals("user2")));
    }

    @Test
    @DisplayName("Username constraint prevents duplicate usernames")
    void usernameConstraintPreventseDuplicates() {
        User user1 = new User(USERNAME, PASSWORD, NOMBRE, APELLIDO, EMAIL, EDAD, GENERO, DOMICILIO, Role.USER);
        User user2 = new User(USERNAME, PASSWORD, "Otro", "Usuario", "otro@test.com", 30, "FEMENINO", "Otra calle", Role.USER);

        entityManager.persistAndFlush(user1);

        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(user2);
        });
    }

    @Test
    @DisplayName("Email constraint prevents duplicate emails")
    void emailConstraintPreventsDuplicates() {
        User user1 = new User("user1", PASSWORD, NOMBRE, APELLIDO, EMAIL, EDAD, GENERO, DOMICILIO, Role.USER);
        User user2 = new User("user2", PASSWORD, "Otro", "Usuario", EMAIL, 30, "FEMENINO", "Otra calle", Role.USER);

        entityManager.persistAndFlush(user1);

        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(user2);
        });
    }

    @Test
    @DisplayName("User can be updated successfully")
    void updateUserSuccess() {
        User user = new User(USERNAME, PASSWORD, NOMBRE, APELLIDO, EMAIL, EDAD, GENERO, DOMICILIO, Role.USER);
        User savedUser = entityManager.persistAndFlush(user);

        savedUser.setEmailVerified(true);
        User updatedUser = userRepository.save(savedUser);

        assertTrue(updatedUser.isEmailVerified());
        assertEquals(savedUser.getId(), updatedUser.getId());
    }

    @Test
    @DisplayName("User can be deleted successfully")
    void deleteUserSuccess() {
        User user = new User(USERNAME, PASSWORD, NOMBRE, APELLIDO, EMAIL, EDAD, GENERO, DOMICILIO, Role.USER);
        User savedUser = entityManager.persistAndFlush(user);
        Long userId = savedUser.getId();

        userRepository.delete(savedUser);
        entityManager.flush();

        Optional<User> deletedUser = userRepository.findById(userId);
        assertFalse(deletedUser.isPresent());
    }

    @Test
    @DisplayName("Find by username is case sensitive")
    void findByUsernameIsCaseSensitive() {
        User user = new User(USERNAME, PASSWORD, NOMBRE, APELLIDO, EMAIL, EDAD, GENERO, DOMICILIO, Role.USER);
        entityManager.persistAndFlush(user);

        Optional<User> result = userRepository.findByUsername(USERNAME.toUpperCase());

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("User authorities are correctly set based on role")
    void userAuthoritiesCorrectlySet() {
        User adminUser = new User("admin", PASSWORD, NOMBRE, APELLIDO, "admin@test.com", EDAD, GENERO, DOMICILIO, Role.ADMIN);
        User regularUser = new User("user", PASSWORD, NOMBRE, APELLIDO, EMAIL, EDAD, GENERO, DOMICILIO, Role.USER);

        entityManager.persist(adminUser);
        entityManager.persist(regularUser);
        entityManager.flush();

        Optional<User> foundAdmin = userRepository.findByUsername("admin");
        Optional<User> foundUser = userRepository.findByUsername("user");

        assertTrue(foundAdmin.isPresent());
        assertTrue(foundUser.isPresent());

        assertTrue(foundAdmin.get().getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(foundUser.get().getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }
}