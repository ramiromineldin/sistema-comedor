package ar.uba.fi.ingsoft1.product_example.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    private EmailService emailService;

    @BeforeEach
    void setup() {
        emailService = new EmailService(javaMailSender);
    }

    @Test
    @DisplayName("Send email succeeds with valid parameters")
    void sendEmailSuccess() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        assertDoesNotThrow(() -> emailService.sendEmail(to, subject, body));

        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Send email with null recipient throws exception")
    void sendEmailNullRecipient() {
        assertThrows(IllegalArgumentException.class, () ->
                emailService.sendEmail(null, "Subject", "Body"));

        verify(javaMailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Send email with empty recipient throws exception")
    void sendEmailEmptyRecipient() {
        assertThrows(IllegalArgumentException.class, () ->
                emailService.sendEmail("", "Subject", "Body"));

        verify(javaMailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Send email with null subject throws exception")
    void sendEmailNullSubject() {
        assertThrows(IllegalArgumentException.class, () ->
                emailService.sendEmail("test@example.com", null, "Body"));

        verify(javaMailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Send email with null body throws exception")
    void sendEmailNullBody() {
        assertThrows(IllegalArgumentException.class, () ->
                emailService.sendEmail("test@example.com", "Subject", null));

        verify(javaMailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Send email handles JavaMailSender exception gracefully")
    void sendEmailHandlesException() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        doThrow(new RuntimeException("Mail server error"))
                .when(javaMailSender).send(any(SimpleMailMessage.class));

        assertThrows(RuntimeException.class, () ->
                emailService.sendEmail(to, subject, body));
    }

    @Test
    @DisplayName("Send email with invalid email format throws exception")
    void sendEmailInvalidFormat() {
        assertThrows(IllegalArgumentException.class, () ->
                emailService.sendEmail("invalid-email", "Subject", "Body"));

        verify(javaMailSender, never()).send(any(SimpleMailMessage.class));
    }
}