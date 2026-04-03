package ar.uba.fi.ingsoft1.product_example.promotions.dto;

import ar.uba.fi.ingsoft1.product_example.promotions.PromotionType;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record PromotionCreateDTO(
        String name,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        PromotionType type,
        BigDecimal discountPercentage,
        BigDecimal discountAmount,
        BigDecimal minimumPurchaseAmount,
        Integer buyQuantity,
        Integer payQuantity,
        DayOfWeek recurringDay,
        LocalTime startTime,
        LocalTime endTime,
        List<PromotionItemCreateDTO> items
) {
}
