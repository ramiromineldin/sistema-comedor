package ar.uba.fi.ingsoft1.product_example.user;

import ar.uba.fi.ingsoft1.product_example.config.security.JwtService;
import ar.uba.fi.ingsoft1.product_example.config.security.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SessionRestController.class)
@Import({SecurityConfig.class, JwtService.class})
class SessionRestControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser
    @DisplayName("Valid Login → 201 Created con tokens")
    void loginOk() throws Exception {
        var tokens = new TokenDTO("access-abc", "refresh-xyz");
        when(userService.loginUser(any(UserLoginDTO.class))).thenReturn(Optional.of(tokens));

        var body = """
                { "username": "user", "password": "Password123", "expectedRole": "USER" }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken", notNullValue()))
                .andExpect(jsonPath("$.refreshToken", notNullValue()));
    }

    @Test
    @WithMockUser
    @DisplayName("Invalid Login → 401 Unauthorized")
    void loginInvalid() throws Exception {
        when(userService.loginUser(any(UserLoginDTO.class))).thenReturn(Optional.empty());

        var body = """
                { "username": "user", "password": "wrong", "expectedRole": "USER" }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("Login with wrong JSON → 400 Bad Request")
    void loginMalformedJson() throws Exception {
        var malformed = "{ \"username\": \"u\", \"password\": pass }";

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformed))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Valid Refresh → 200 OK con tokens")
    void refreshOk() throws Exception {
        var tokens = new TokenDTO("access-2", "refresh-2");
        when(userService.refresh(any(RefreshDTO.class))).thenReturn(Optional.of(tokens));

        var body = """
                { "refreshToken": "refresh-xyz" }
                """;

        mockMvc.perform(put("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", notNullValue()))
                .andExpect(jsonPath("$.refreshToken", notNullValue()));
    }

    @Test
    @WithMockUser
    @DisplayName("Invalid Refresh → 401 Unauthorized")
    void refreshInvalid() throws Exception {
        when(userService.refresh(any(RefreshDTO.class))).thenReturn(Optional.empty());

        var body = """
                { "refreshToken": "bad-token" }
                """;

        mockMvc.perform(put("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("Login with missing username → 400 Bad Request")
    void loginMissingUsername() throws Exception {
        var body = """
            { "password": "Password123", "expectedRole": "USER" }
            """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Login with missing password → 400 Bad Request")
    void loginMissingPassword() throws Exception {
        var body = """
            { "username": "user", "expectedRole": "USER" }
            """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Login with empty username → 400 Bad Request")
    void loginEmptyUsername() throws Exception {
        var body = """
            { "username": "", "password": "Password123", "expectedRole": "USER" }
            """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Login with empty password → 400 Bad Request")
    void loginEmptyPassword() throws Exception {
        var body = """
            { "username": "user", "password": "", "expectedRole": "USER" }
            """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Refresh with missing token → 400 Bad Request")
    void refreshMissingToken() throws Exception {
        var body = """
            { }
            """;

        mockMvc.perform(put("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Refresh with empty token → 400 Bad Request")
    void refreshEmptyToken() throws Exception {
        var body = """
            { "refreshToken": "" }
            """;

        mockMvc.perform(put("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Login with wrong HTTP method → 405 Method Not Allowed")
    void loginWrongMethod() throws Exception {
        mockMvc.perform(put("/auth/login"))  // Cambiar get por put
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @WithMockUser
    @DisplayName("Refresh with wrong HTTP method → 405 Method Not Allowed")
    void refreshWrongMethod() throws Exception {
        mockMvc.perform(post("/auth/refresh"))
                .andExpect(status().isMethodNotAllowed());
    }
}
