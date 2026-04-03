package ar.uba.fi.ingsoft1.product_example.menu;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/menu")
@Validated
@RequiredArgsConstructor
@Tag(name = "MenuAdmin")
@PreAuthorize("hasRole('ADMIN')")
public class MenuComedorRestController {

    private final MenuComedorService service;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody MenuComedorCreateDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.getAdminById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<?> deactivate(@PathVariable Long id) {
        try {
            service.deactivate(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/productos/{productId}")
    public ResponseEntity<?> addProduct(@PathVariable Long id, @PathVariable Long productId) {
        try {
            return ResponseEntity.ok(service.addProduct(id, productId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/productos/{productId}")
    public ResponseEntity<?> removeProduct(@PathVariable Long id, @PathVariable Long productId) {
        try {
            return ResponseEntity.ok(service.removeProduct(id, productId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
