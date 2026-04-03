package ar.uba.fi.ingsoft1.product_example.menu;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/menu")
@Validated
@RequiredArgsConstructor
@Tag(name = "MenuUser")
@PreAuthorize("hasRole('USER')")
public class MenuComedorUserController {

    private final MenuComedorService service;


    @GetMapping("/hoy")
    public ResponseEntity<?> getMenuHoy() {
        try {
            return ResponseEntity.ok(service.getMenuHoy());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}