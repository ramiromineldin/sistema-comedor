package ar.uba.fi.ingsoft1.product_example.promotions;

import ar.uba.fi.ingsoft1.product_example.promotions.dto.PromotionCreateDTO;
import ar.uba.fi.ingsoft1.product_example.promotions.dto.PromotionDTO;
import ar.uba.fi.ingsoft1.product_example.promotions.dto.PromotionItemCreateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/promotions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class PromotionAdminRestController {

    private final PromotionService promotionService;

    @GetMapping
    public ResponseEntity<?> listAll() {
        return ResponseEntity.ok(promotionService.getAllPromotions());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody PromotionCreateDTO request) {
        try {
            PromotionDTO created = promotionService.createPromotion(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PromotionCreateDTO request) {
        try {
            PromotionDTO updated = promotionService.updatePromotion(id, request);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<?> activate(@PathVariable Long id) {
        try {
            PromotionDTO promotionDTO = promotionService.activatePromotion(id);
            return ResponseEntity.ok().body(promotionDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/add-item")
    public ResponseEntity<?> addItem(@PathVariable Long id, @RequestBody PromotionItemCreateDTO itemRequest) {
        try {
            PromotionDTO updated = promotionService.addItemToPromotion(id, itemRequest);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/remove-item/{itemId}")
    public ResponseEntity<?> removeItem(@PathVariable Long id, @PathVariable Long itemId) {
        try {
            PromotionDTO updated = promotionService.removeItemFromPromotion(id, itemId);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/desactivate")
    public ResponseEntity<?> desactivate(@PathVariable Long id) {
        try {
            PromotionDTO promotionDTO = promotionService.desactivatePromotion(id);
            return ResponseEntity.ok().body(promotionDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            promotionService.deletePromotion(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
