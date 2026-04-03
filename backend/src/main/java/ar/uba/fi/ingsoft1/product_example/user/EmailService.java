package ar.uba.fi.ingsoft1.product_example.user;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;


    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String text) {
        if (to == null) {
            throw new IllegalArgumentException("El destinatario no puede ser nulo");
        }
        
        if (to.trim().isEmpty()) {
            throw new IllegalArgumentException("El destinatario no puede estar vacío");
        }
        
        if (subject == null) {
            throw new IllegalArgumentException("El asunto no puede ser nulo");
        }
        
        if (text == null) {
            throw new IllegalArgumentException("El cuerpo del mensaje no puede ser nulo");
        }
        
        if (!isValidEmail(to)) {
            throw new IllegalArgumentException("Formato de email inválido");
        }
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
    
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".") && email.indexOf("@") > 0 
               && email.indexOf("@") < email.lastIndexOf(".");
    }
}

