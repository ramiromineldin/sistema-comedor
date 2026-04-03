package ar.uba.fi.ingsoft1.product_example.user;

import ar.uba.fi.ingsoft1.product_example.common.exception.InvalidPasswordException;
import ar.uba.fi.ingsoft1.product_example.common.exception.UserAlreadyExistsException;
import ar.uba.fi.ingsoft1.product_example.config.security.JwtService;
import ar.uba.fi.ingsoft1.product_example.user.refresh_token.EmailVerificationToken;
import ar.uba.fi.ingsoft1.product_example.user.refresh_token.EmailVerificationTokenRepository;
import ar.uba.fi.ingsoft1.product_example.user.refresh_token.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ar.uba.fi.ingsoft1.product_example.user.password_reset.PasswordResetTokenRepository;
import org.springframework.test.util.ReflectionTestUtils;


import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private UserService userService;
    private EmailVerificationTokenRepository emailVerificationTokenRepository = mock();
    private static final String USERNAME = "user";
    private static final String PASSWORD = "password";
    private static final String NOMBRE = "nombre";
    private static final String APELLIDO = "apellido";
    private static final String EMAIL = "nombreapellido@gmail.com";
    private static final Integer EDAD = 20;
    private static final String GENERO = "MASCULINO";
    private static final String DOMICILIO = "CALLE 123";

    @BeforeEach
    void setup() {
        var passwordEncoder = new BCryptPasswordEncoder();
        var passwordHash = passwordEncoder.encode(PASSWORD);

        JavaMailSender mockMailSender = mock(JavaMailSender.class);
        EmailService emailService = new EmailService(mockMailSender);

        emailVerificationTokenRepository = mock(EmailVerificationTokenRepository.class);
        PasswordResetTokenRepository passwordResetTokenRepository = mock(PasswordResetTokenRepository.class);

        UserRepository userRepository = mock();
        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.empty());
        when(userRepository.findByUsername(USERNAME))
                .thenReturn(Optional.of(new User(
                        USERNAME, passwordHash, NOMBRE, APELLIDO, EMAIL, EDAD, GENERO, DOMICILIO, Role.USER
                )));

        var key = "0".repeat(64);
        userService = new UserService(
                new JwtService(key, 1L),
                new BCryptPasswordEncoder(),
                userRepository,
                new RefreshTokenService(1L, 20, mock()),
                emailService,
                emailVerificationTokenRepository,
                passwordResetTokenRepository
        );
        ReflectionTestUtils.setField(userService, "externalUrl", "http://localhost:5173");
    }


    // LOGIN

    @Test
    @DisplayName("Login succedes with valid credentials")
    void loginUser() {
        var response = userService.loginUser(new UserLoginDTO(USERNAME, PASSWORD, Role.USER));
        assertNotNull(response.orElseThrow());
    }

    @Test
    @DisplayName("Login fails with wrong password")
    void loginWithWrongPassword() {
        var response = userService.loginUser(new UserLoginDTO(USERNAME, PASSWORD + "_wrong", Role.USER));
        assertEquals(Optional.empty(), response);
    }

    @Test
    @DisplayName("Login fails with nonexistent username")
    void loginNonexistentUser() {
        var response = userService.loginUser(new UserLoginDTO(USERNAME + "_wrong", PASSWORD, Role.USER));
        assertEquals(Optional.empty(), response);
    }

    @Test
    @DisplayName("Login fails with wrong role")
    void loginWithWrongRole() {
        var response = userService.loginUser(new UserLoginDTO(USERNAME, PASSWORD, Role.ADMIN));
        assertEquals(Optional.empty(), response);
    }

    @Test
    @DisplayName("Login fails with empty username")
    void loginWithEmptyUser() {
        var response = userService.loginUser(new UserLoginDTO("", PASSWORD, Role.USER));
        assertEquals(Optional.empty(), response);
    }

    @Test
    @DisplayName("Login fails with null username")
    void loginWithNullUser() {
        var response = userService.loginUser(new UserLoginDTO(null, PASSWORD, Role.USER));
        assertEquals(Optional.empty(), response);
    }

    @Test
    @DisplayName("Login fails with empty password")
    void loginWithEmptyPassword() {
        var response = userService.loginUser(new UserLoginDTO(USERNAME, "", Role.USER));
        assertEquals(Optional.empty(), response);
    }

    @Test
    @DisplayName("Login throws IllegalArgumentException with null password")
    void loginWithNullPassword() {
        assertThrows(IllegalArgumentException.class,  () -> userService.loginUser(new UserLoginDTO(USERNAME, null, Role.USER)) );
    }

    // REGISTRATION TESTS
    @Test
    @DisplayName("Register succeeds with valid data")
    void registerValidUser() {
        UserCreateDTO validUser = new UserCreateDTO(
                "Jorge", "Goodpassword123", NOMBRE, APELLIDO, EMAIL, EDAD, GENERO, DOMICILIO, Role.USER
        );

        assertDoesNotThrow(() -> userService.createUser(validUser));
    }

    @Test
    @DisplayName("Register fails when username already exists")
    void registerExistingUsername() {
        UserCreateDTO existingUser = new UserCreateDTO(
                USERNAME, PASSWORD, NOMBRE, APELLIDO, EMAIL, EDAD, GENERO, DOMICILIO, Role.USER
        );

        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.createUser(existingUser)
        );

        assertTrue(exception.getMessage().contains(USERNAME));
    }

    @Test
    @DisplayName("Register fails with weak password")
    void registerWeakPassword() {
        UserCreateDTO userWithWeakPassword = new UserCreateDTO(
                "Vicky", "weak", NOMBRE, APELLIDO, EMAIL, EDAD, GENERO, DOMICILIO, Role.USER
        );

        assertThrows(InvalidPasswordException.class,
                () -> userService.createUser(userWithWeakPassword));
    }

    @Test
    @DisplayName("Register fails with short password")
    void registerShortPassword() {
        UserCreateDTO userWithShortPassword = new UserCreateDTO(
                "Gabriela", "Pass1", NOMBRE, APELLIDO, EMAIL, EDAD, GENERO, DOMICILIO, Role.USER
        );

        assertThrows(InvalidPasswordException.class,
                () -> userService.createUser(userWithShortPassword));
    }

    @Test
    @DisplayName("Register fails with password missing uppercase letter")
    void registerNoUppercase() {
        UserCreateDTO userWithoutUppercase = new UserCreateDTO(
                "Estefano", "password123", NOMBRE, APELLIDO, EMAIL, EDAD, GENERO, DOMICILIO, Role.USER
        );


        assertThrows(InvalidPasswordException.class,
                () -> userService.createUser(userWithoutUppercase));
    }

    @Test
    @DisplayName("Register fails with password missing numbers")
    void registerNoNumbers() {
        UserCreateDTO userWithoutNumbers = new UserCreateDTO(
                "Ramiro", "PasswordOnly", NOMBRE, APELLIDO, EMAIL, EDAD, GENERO, DOMICILIO, Role.USER
        );

        assertThrows(InvalidPasswordException.class,
                () -> userService.createUser(userWithoutNumbers));
    }

    // PASSWORD VALIDATION

    @Test
    @DisplayName("Password must have at least 8 characters")
    void passwordTooShort() {
        UserCreateDTO userWith7Chars = new UserCreateDTO(
               "Wendy", "Pass12A", NOMBRE, APELLIDO, EMAIL, EDAD, GENERO, DOMICILIO, Role.USER
        );

        assertThrows(InvalidPasswordException.class,
                () -> userService.createUser(userWith7Chars));
    }

    @Test
    @DisplayName("Password must include at least one uppercase letter")
    void passwordNoUppercase() {
        UserCreateDTO userWithoutUppercase = new UserCreateDTO(
                "Agustin", "password123", NOMBRE, APELLIDO, EMAIL, EDAD, GENERO, DOMICILIO, Role.USER
        );

        assertThrows(InvalidPasswordException.class,
                () -> userService.createUser(userWithoutUppercase));
    }

    @Test
    @DisplayName("Password is valid – should not throw exception")
    void passwordNoNumber() {
        UserCreateDTO userWithoutNumbers = new UserCreateDTO(
                "Nicolas", "PasswordABC", NOMBRE, APELLIDO, EMAIL, EDAD, GENERO, DOMICILIO, Role.USER
        );

        assertThrows(InvalidPasswordException.class,
                () -> userService.createUser(userWithoutNumbers));
    }

    @Test
    @DisplayName("Password is valid – should not throw exception")
    void passwordValid() {
        UserCreateDTO userWithValidPassword = new UserCreateDTO(
                "Dana", "ValidPass123", NOMBRE, APELLIDO, EMAIL, EDAD, GENERO, DOMICILIO, Role.USER
        );

        assertDoesNotThrow(() -> userService.createUser(userWithValidPassword));
    }

    @Test
    @DisplayName("Password validation fails with null password")
    void passwordNull() {
        UserCreateDTO userWithNullPassword = new UserCreateDTO(
                "TestUser", null, NOMBRE, APELLIDO, EMAIL, EDAD, GENERO, DOMICILIO, Role.USER
        );

        assertThrows(InvalidPasswordException.class,
                () -> userService.createUser(userWithNullPassword));
    }

    @Test
    @DisplayName("Password validation fails with whitespace-only password")
    void passwordOnlyWhitespace() {
        UserCreateDTO userWithWhitespacePassword = new UserCreateDTO(
                "TestUser", "        ", NOMBRE, APELLIDO, EMAIL, EDAD, GENERO, DOMICILIO, Role.USER
        );

        assertThrows(InvalidPasswordException.class,
                () -> userService.createUser(userWithWhitespacePassword));
    }

    //EDGE CASES

    @Test
    @DisplayName("Register fails with null data")
    void registerNullData() {
        assertThrows(Exception.class, () -> userService.createUser(null));
    }

    @Test
    @DisplayName("Verify email returns false with null token")
    void verifyEmailNullToken() {
        boolean result = userService.verifyEmail(null);

        assertFalse(result);
    }

    @Test
    @DisplayName("Verify email returns false with empty token")
    void verifyEmailEmptyToken() {
        boolean result = userService.verifyEmail("");

        assertFalse(result);
    }

    @Test
    @DisplayName("Register fails with null username")
    void registerNullUsername() {
        UserCreateDTO userWithNullUsername = new UserCreateDTO(
                null, "ValidPass123", NOMBRE, APELLIDO, EMAIL, EDAD, GENERO, DOMICILIO, Role.USER
        );

        assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(userWithNullUsername));
    }

    @Test
    @DisplayName("Register fails with empty username")
    void registerEmptyUsername() {
        UserCreateDTO userWithEmptyUsername = new UserCreateDTO(
                "", "ValidPass123", NOMBRE, APELLIDO, EMAIL, EDAD, GENERO, DOMICILIO, Role.USER
        );

        assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(userWithEmptyUsername));
    }

    // EMAIL VERIFICATION

    @Test
    @DisplayName("Email with valid token should return true")
    void validToken() {
        String validToken = "valid-token";
        User user = new User(USERNAME, PASSWORD, NOMBRE, APELLIDO, EMAIL, EDAD, GENERO, DOMICILIO, Role.USER);
        EmailVerificationToken verificationToken = new EmailVerificationToken(
                validToken, user, LocalDateTime.now().plusHours(1)
        );

        when(emailVerificationTokenRepository.findByToken(validToken))
                .thenReturn(Optional.of(verificationToken));

        boolean result = userService.verifyEmail(validToken);

        assertTrue(result);
    }

    @Test
    @DisplayName("Email with invalid token should return false")
    void invalidToken() {
        String invalidToken = "invalid-token";
        when(emailVerificationTokenRepository.findByToken(invalidToken))
                .thenReturn(Optional.empty());

        boolean result = userService.verifyEmail(invalidToken);

        assertFalse(result);
    }

    @Test
    @DisplayName("Email with expired token should return false")
    void expiredToken() {
        String expiredToken = "expired-token";
        User user = new User(USERNAME, PASSWORD, NOMBRE, APELLIDO, EMAIL, EDAD, GENERO, DOMICILIO, Role.USER);
        EmailVerificationToken expiredVerificationToken = new EmailVerificationToken(
                expiredToken, user, LocalDateTime.now().minusHours(1)
        );

        when(emailVerificationTokenRepository.findByToken(expiredToken))
                .thenReturn(Optional.of(expiredVerificationToken));

        boolean result = userService.verifyEmail(expiredToken);

        assertFalse(result);
    }

    @Test
    @DisplayName("Email with already verified token should return true")
    void alreadyVerifiedToken() {
        String alreadyVerifiedToken = "verified-token";
        User user = new User(USERNAME, PASSWORD, NOMBRE, APELLIDO, EMAIL, EDAD, GENERO, DOMICILIO, Role.USER);
        EmailVerificationToken verificationToken = new EmailVerificationToken(
                alreadyVerifiedToken, user, LocalDateTime.now().plusHours(1)
        );
        verificationToken.setVerified(true);

        when(emailVerificationTokenRepository.findByToken(alreadyVerifiedToken))
                .thenReturn(Optional.of(verificationToken));

        boolean result = userService.verifyEmail(alreadyVerifiedToken);

        assertTrue(result);
    }

    // LOAD USER BY USERNAME TESTS
    @Test
    @DisplayName("Load user by username succeeds with existing user")
    void loadUserByUsernameSuccess() {
        UserDetails userDetails = userService.loadUserByUsername(USERNAME);

        assertNotNull(userDetails);
        assertEquals(USERNAME, userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    @DisplayName("Load user by username fails with nonexistent user")
    void loadUserByUsernameNotFound() {
        assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("nonexistent"));
    }

    @Test
    @DisplayName("Find by username returns correct user")
    void findByUsernameReturnsUser() {
        User found = userService.findByUsername(USERNAME);

        assertNotNull(found);
        assertEquals(USERNAME, found.getUsername());
        assertEquals(EMAIL, found.getEmail());
    }

    @Test
    @DisplayName("Request password reset sends email")
    void requestPasswordResetSendsEmail() {
        assertDoesNotThrow(() -> userService.requestPasswordReset(EMAIL));
    }

}
