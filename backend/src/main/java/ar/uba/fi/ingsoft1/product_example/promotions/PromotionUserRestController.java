package ar.uba.fi.ingsoft1.product_example.promotions;

import ar.uba.fi.ingsoft1.product_example.promotions.dto.PromotionDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/promotions")
@Validated
@RequiredArgsConstructor
@Tag(name = "PromotionsUser")
@PreAuthorize("hasRole('USER')")
public class PromotionUserRestController {

    private final PromotionService promotionService;

    @GetMapping
    public ResponseEntity<List<PromotionDTO>> listActive() {
        return ResponseEntity.ok(promotionService.getActivePromotionsForStudents());
    }
}


