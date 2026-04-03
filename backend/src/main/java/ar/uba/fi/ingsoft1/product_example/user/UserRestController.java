package ar.uba.fi.ingsoft1.product_example.user;

import ar.uba.fi.ingsoft1.product_example.common.exception.InvalidPasswordException;
import ar.uba.fi.ingsoft1.product_example.common.exception.UserAlreadyExistsException;
import ar.uba.fi.ingsoft1.product_example.user.password_reset.ForgotRequestDTO;
import ar.uba.fi.ingsoft1.product_example.user.password_reset.ResetRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Tag(name = "1 - Users")
class UserRestController {
    private final UserService userService;

    @Autowired
    UserRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/signup", produces = "application/json")
    @Operation(summary = "Create a new user")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode = "409", description = "User already exists", content = @Content)
    ResponseEntity<String> signUp(
            @Valid @NonNull @RequestBody UserCreateDTO data
    ) throws MethodArgumentNotValidException {
        try{
            userService.createUser(data);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (InvalidPasswordException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/verify")
    @Operation(summary = "Verify the user's email")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        boolean verified = userService.verifyEmail(token);
        if (verified) {
            return ResponseEntity.ok("Tu cuenta fue verificada correctamente.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("El enlace de verificación es inválido o ha expirado.");
        }
    }

    @PostMapping(value = "/password/forgot")
    @Operation(summary = "Solicita link de reseteo de contraseña (si el email existe)")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotRequestDTO req) {
        userService.requestPasswordReset(req.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/password/reset")
    @Operation(summary = "Resetea contraseña con token")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetRequestDTO req) {
        try {
            userService.resetPassword(req.token(), req.newPassword());
            return ResponseEntity.ok("Contraseña actualizada");
        } catch (InvalidPasswordException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
