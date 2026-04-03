package ar.uba.fi.ingsoft1.product_example.combos;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/combos")
@Validated
@RequiredArgsConstructor
@Tag(name = "Combos")
@PreAuthorize("hasRole('ADMIN')")
public class ComboRestController {

    private final ComboService comboService;

    @Operation(summary = "Crear nuevo combo")
    @PostMapping
    public ResponseEntity<?> createCombo(@Valid @RequestBody ComboCreateDTO dto) {
        try {
            ComboDTO combo = comboService.createCombo(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(combo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el combo: " + e.getMessage());
        }
    }

    @Operation(summary = "Obtener un combo por ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getComboById(@PathVariable Long id) {
        Optional<ComboDTO> combo = comboService.getComboById(id);
        if (combo.isPresent()) {
            return ResponseEntity.ok(combo.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Combo no encontrado");
        }
    }

    @Operation(summary = "Obtener todos los combos")
    @GetMapping
    public ResponseEntity<List<ComboDTO>> getAllCombos() {
        List<ComboDTO> combos = comboService.getAllCombos();
        return ResponseEntity.ok(combos);
    }

    @Operation(summary = "Buscar combos por nombre")
    @GetMapping("/search")
    public ResponseEntity<List<ComboDTO>> searchCombosByName(@RequestParam String name) {
        List<ComboDTO> combos = comboService.searchCombosByName(name);
        return ResponseEntity.ok(combos);
    }

    @Operation(summary = "Agregar producto a un combo")
    @PostMapping("/{comboId}/productos/{productId}")
    public ResponseEntity<?> addProductToCombo(
            @PathVariable Long comboId,
            @PathVariable Long productId) {
        try {
            ComboDTO updatedCombo = comboService.addProductToCombo(comboId, productId);
            return ResponseEntity.ok(updatedCombo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Eliminar producto de un combo")
    @DeleteMapping("/{comboId}/productos/{productId}")
    public ResponseEntity<?> removeProductFromCombo(
            @PathVariable Long comboId,
            @PathVariable Long productId) {
        try {
            ComboDTO updatedCombo = comboService.removeProductFromCombo(comboId, productId);
            return ResponseEntity.ok(updatedCombo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Actualizar descuento de un combo")
    @PatchMapping("/{id}/descuento")
    public ResponseEntity<?> updateDiscount(
            @PathVariable Long id,
            @RequestParam BigDecimal discount) {
        try {
            if (discount.compareTo(BigDecimal.ZERO) < 0 || discount.compareTo(BigDecimal.ONE) > 0) {
                return ResponseEntity.badRequest()
                        .body("El descuento debe estar entre 0 y 1");
            }
            ComboDTO updatedCombo = comboService.updateComboDiscount(id, discount);
            return ResponseEntity.ok(updatedCombo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Eliminar un combo")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCombo(@PathVariable Long id) {
        try {
            comboService.deleteCombo(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el combo: " + e.getMessage());
        }
    }
}