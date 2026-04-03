package ar.uba.fi.ingsoft1.product_example.user;

import ar.uba.fi.ingsoft1.product_example.common.exception.InvalidPasswordException;
import ar.uba.fi.ingsoft1.product_example.common.exception.UserAlreadyExistsException;
import ar.uba.fi.ingsoft1.product_example.config.security.JwtService;
import ar.uba.fi.ingsoft1.product_example.config.security.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserRestController.class)
@Import({SecurityConfig.class, JwtService.class})
class UserRestControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "Password123";
    private static final String NOMBRE = "Juan";
    private static final String APELLIDO = "Perez";
    private static final String EMAIL = "juan.perez@gmail.com";
    private static final Integer EDAD = 25;
    private static final String GENERO = "MASCULINO";
    private static final String DOMICILIO = "Av. Corrientes 1234";

    @Test
    @WithMockUser
    @DisplayName("GET /users/signup → 405 Method Not Allowed")
    void signupGetShouldBe405() throws Exception {
        mockMvc.perform(get("/users/signup"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @WithMockUser
    @DisplayName("Valid signup → 201 Created")
    void signupValid() throws Exception {
        var dto = new UserCreateDTO(
                USERNAME, PASSWORD, NOMBRE, APELLIDO, EMAIL, EDAD, GENERO, DOMICILIO, Role.USER
        );
        doNothing().when(userService).createUser(any(UserCreateDTO.class));

        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    @DisplayName("User already exists → 409 Conflict with message")
    void signupExistingUser() throws Exception {
        var dto = new UserCreateDTO(
                USERNAME, PASSWORD, NOMBRE, APELLIDO, EMAIL, EDAD, GENERO, DOMICILIO, Role.USER
        );
        doThrow(new UserAlreadyExistsException(USERNAME))
                .when(userService).createUser(any(UserCreateDTO.class));

        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString(USERNAME)));
    }

    @Test
    @WithMockUser
    @DisplayName("Invalid password → 400 Bad Request")
    void signupInvalidPassword() throws Exception {
        var dto = new UserCreateDTO(
                USERNAME, "weak", NOMBRE, APELLIDO, EMAIL, EDAD, GENERO, DOMICILIO, Role.USER
        );
        doThrow(new InvalidPasswordException())
                .when(userService).createUser(any(UserCreateDTO.class));

        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("JSON is wrong → 400 Bad Request")
    void signupMalformedJson() throws Exception {
        var malformed = "{ \"username\": \"u\", \"password\": Password123 }";

        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformed))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Good Email Verification → 200 OK with message")
    void verifyEmailValid() throws Exception {
        var token = "valid-token";
        org.mockito.Mockito.when(userService.verifyEmail(token)).thenReturn(true);

        mockMvc.perform(get("/users/verify").param("token", token))
                .andExpect(status().isOk())
                .andExpect(content().string("Tu cuenta fue verificada correctamente."));
    }

    @Test
    @WithMockUser
    @DisplayName("Invalid email verification → 400 Bad Request with message")
    void verifyEmailInvalid() throws Exception {
        var token = "invalid-token";
        org.mockito.Mockito.when(userService.verifyEmail(token)).thenReturn(false);

        mockMvc.perform(get("/users/verify").param("token", token))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El enlace de verificación es inválido o ha expirado."));
    }

    @Test
    @WithMockUser
    @DisplayName("Signup with null age → 400 Bad Request")
    void signupNullAge() throws Exception {
        var json = String.format("""
            {
                "username": "%s",
                "password": "%s",
                "nombre": "%s",
                "apellido": "%s",
                "email": "%s",
                "edad": null,
                "genero": "%s",
                "domicilio": "%s",
                "role": "USER"
            }
            """, USERNAME, PASSWORD, NOMBRE, APELLIDO, EMAIL, GENERO, DOMICILIO);

        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Signup with empty username → 400 Bad Request")
    void signupEmptyUsername() throws Exception {
        var dto = new UserCreateDTO(
                "", PASSWORD, NOMBRE, APELLIDO, EMAIL, EDAD, GENERO, DOMICILIO, Role.USER
        );

        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Signup with empty password → 400 Bad Request")
    void signupEmptyPassword() throws Exception {
        var dto = new UserCreateDTO(
                USERNAME, "", NOMBRE, APELLIDO, EMAIL, EDAD, GENERO, DOMICILIO, Role.USER
        );

        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Signup with invalid email format → 400 Bad Request")
    void signupInvalidEmail() throws Exception {
        var dto = new UserCreateDTO(
                USERNAME, PASSWORD, NOMBRE, APELLIDO, "invalid-email", EDAD, GENERO, DOMICILIO, Role.USER
        );

        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Signup with unsupported media type → 415 Unsupported Media Type")
    void signupUnsupportedMediaType() throws Exception {
        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("not json"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @WithMockUser
    @DisplayName("Signup with generic exception → 500 Internal Server Error")
    void signupGenericException() throws Exception {
        var dto = new UserCreateDTO(
                USERNAME, PASSWORD, NOMBRE, APELLIDO, EMAIL, EDAD, GENERO, DOMICILIO, Role.USER
        );
        doThrow(new RuntimeException("Database error"))
                .when(userService).createUser(any(UserCreateDTO.class));

        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Database error"));
    }

    @Test
    @WithMockUser
    @DisplayName("Email verification without token → 400 Bad Request")
    void verifyEmailNoToken() throws Exception {
        mockMvc.perform(get("/users/verify"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Email verification with empty token → 400 Bad Request")
    void verifyEmailEmptyToken() throws Exception {
        mockMvc.perform(get("/users/verify").param("token", ""))
                .andExpect(status().isBadRequest());
    }
}
