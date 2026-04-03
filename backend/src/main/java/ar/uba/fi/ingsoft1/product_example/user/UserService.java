package ar.uba.fi.ingsoft1.product_example.user;

import ar.uba.fi.ingsoft1.product_example.common.exception.InvalidPasswordException;
import ar.uba.fi.ingsoft1.product_example.common.exception.UserAlreadyExistsException;
import ar.uba.fi.ingsoft1.product_example.config.security.JwtService;
import ar.uba.fi.ingsoft1.product_example.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.product_example.user.password_reset.PasswordResetToken;
import ar.uba.fi.ingsoft1.product_example.user.password_reset.PasswordResetTokenRepository;
import ar.uba.fi.ingsoft1.product_example.user.refresh_token.EmailVerificationToken;
import ar.uba.fi.ingsoft1.product_example.user.refresh_token.EmailVerificationTokenRepository;
import ar.uba.fi.ingsoft1.product_example.user.refresh_token.RefreshToken;
import ar.uba.fi.ingsoft1.product_example.user.refresh_token.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Value("${app.external-url}")
    private String externalUrl;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> {
                    var msg = String.format("Username '%s' not found", username);
                    return new UsernameNotFoundException(msg);
                });
    }

    public void createUser(UserCreateDTO data) {
        if (data.username() == null) {
            throw new IllegalArgumentException("El nombre de usuario no puede ser nulo");
        }
        if (data.username().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío");
        }
        if (userRepository.findByUsername(data.username()).isPresent()) {
           throw new UserAlreadyExistsException(data.username());
        }
        if(!isValidPassword(data.password())){
            throw new InvalidPasswordException();
        }
        User newUser = data.asUser(passwordEncoder::encode);
        userRepository.save(newUser);

        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = new EmailVerificationToken(
                token, newUser, LocalDateTime.now().plusHours(24)
        );
        emailVerificationTokenRepository.save(verificationToken);


        String link = UriComponentsBuilder
                .fromUriString(externalUrl)
                .path("/users/verify")
                .queryParam("token", token)
                .toUriString();
        String body = "Hola " + newUser.getUsername() + "!\n\n" +
                "Por favor verifica tu cuenta haciendo clic en el siguiente enlace:\n" +
                link + "\n\nEste enlace expirará en 24 horas.";
        emailService.sendEmail(newUser.getEmail(), "Verifica tu cuenta", body);
    }

    private boolean isValidPassword(String password) {
        return password != null  &&
                password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*\\d.*");
    }

    public boolean verifyEmail(String token) {
        Optional<EmailVerificationToken> opt = emailVerificationTokenRepository.findByToken(token);
        if (opt.isEmpty()) return false;

        EmailVerificationToken verificationToken = opt.get();
        if (verificationToken.isExpired()) {
            emailVerificationTokenRepository.delete(verificationToken);
            return false;
        }

        if(verificationToken.isVerified()) return true;

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);
        verificationToken.setVerified(true);
        emailVerificationTokenRepository.save(verificationToken);

        return true;
    }

    Optional<TokenDTO> loginUser(UserCredentials data) {
        Optional<User> maybeUser = userRepository.findByUsername(data.username());
        return maybeUser
                .filter(user -> passwordEncoder.matches(data.password(), user.getPassword()))
                .filter(user -> user.getRole() == data.expectedRole())
                .map(this::generateTokens);
    }

    Optional<TokenDTO> refresh(RefreshDTO data) {
        return refreshTokenService.findByValue(data.refreshToken())
                .map(RefreshToken::user)
                .map(this::generateTokens);
    }

    private TokenDTO generateTokens(User user) {
        String accessToken = jwtService.createToken(new JwtUserDetails(
                user.getUsername(),
                user.getRole().name()
        ));
        RefreshToken refreshToken = refreshTokenService.createFor(user);
        return new TokenDTO(accessToken, refreshToken.value());
    }

    public void requestPasswordReset(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            String newToken = UUID.randomUUID().toString();
            LocalDateTime newExpiry = LocalDateTime.now().plusHours(1);

            passwordResetTokenRepository.findByUserId(user.getId())
                    .ifPresentOrElse(prt -> {
                                prt.setToken(newToken);
                                prt.setUsed(false);
                                prt.setExpiryDate(newExpiry);
                                passwordResetTokenRepository.save(prt);
                            },
                            () -> {
                                var prt = new PasswordResetToken(newToken, user, newExpiry);
                                passwordResetTokenRepository.save(prt);
                            });

            String link = UriComponentsBuilder
                    .fromUriString(externalUrl)
                    .path("/users/password/reset")
                    .queryParam("token", newToken)
                    .toUriString();
            String body = "Hola " + user.getUsername() + "!\n\n" +
                    "Hacé clic para restablecer tu contraseña:\n" + link +
                    "\n\nEste enlace expira en 1 hora.";
            emailService.sendEmail(user.getEmail(), "Restablecer contraseña", body);
        });
    }

    public void resetPassword(String token, String newPassword) {
        var opt = passwordResetTokenRepository.findByToken(token);
        if (opt.isEmpty()) { throw new IllegalArgumentException("Token inválido"); }

        var prt = opt.get();
        if (prt.isUsed() || prt.isExpired()) {
            throw new IllegalArgumentException("Token inválido o expirado");
        }

        if (!isValidPassword(newPassword)) {
            throw new InvalidPasswordException();
        }

        var user = prt.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        prt.setUsed(true);
        passwordResetTokenRepository.save(prt);
    }
    public User findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con email: " + email));
    }

    public User findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El username es obligatorio");
        }
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con username: " + username));
    }
}
