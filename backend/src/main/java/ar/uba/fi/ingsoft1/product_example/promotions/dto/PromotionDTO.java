package ar.uba.fi.ingsoft1.product_example.promotions.dto;

import ar.uba.fi.ingsoft1.product_example.promotions.Promotion;
import ar.uba.fi.ingsoft1.product_example.promotions.PromotionType;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;

public record PromotionDTO (
        Long id,
        String name,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        boolean active,
        PromotionType type,
        BigDecimal originalPrice,
        BigDecimal finalPrice,
        BigDecimal discountPercentage,
        BigDecimal discountAmount,
        BigDecimal minimumPurchaseAmount,
        Integer buyQuantity,
        Integer payQuantity,
        DayOfWeek recurringDay,
        LocalTime startTime,
        LocalTime endTime,
        boolean available,
        List<PromotionItemDTO> items
){
    public static PromotionDTO fromPromotion(Promotion promotion) {
        List<PromotionItemDTO> items = promotion.getItems()
                .stream()
                .map(PromotionItemDTO::fromPromotionItem)
                .collect(Collectors.toList());

        // Usar los precios ya calculados en la entidad Promotion
        BigDecimal originalPrice = promotion.getOriginalPrice() != null
            ? promotion.getOriginalPrice()
            : BigDecimal.ZERO;

        BigDecimal finalPrice = promotion.getFinalPrice() != null
            ? promotion.getFinalPrice()
            : BigDecimal.ZERO;

        // Calcular si la promoción está disponible ahora
        boolean available = isPromotionAvailable(promotion);

        return new PromotionDTO(
                promotion.getId(),
                promotion.getName(),
                promotion.getDescription(),
                promotion.getStartDate(),
                promotion.getEndDate(),
                promotion.isActive(),
                promotion.getType(),
                originalPrice,
                finalPrice,
                promotion.getDiscountPercentage(),
                promotion.getDiscountAmount(),
                promotion.getMinimumPurchaseAmount(),
                promotion.getBuyQuantity(),
                promotion.getPayQuantity(),
                promotion.getRecurringDay(),
                promotion.getStartTime(),
                promotion.getEndTime(),
                available,
                items
        );
    }

    private static boolean isPromotionAvailable(Promotion promotion) {
        var now = LocalDateTime.now();
        var today = now.getDayOfWeek();
        var currentTime = now.toLocalTime();

        // Verificar día de la semana
        if (promotion.getRecurringDay() != null) {
            if (!promotion.getRecurringDay().equals(today)) {
                return false;
            }
        }

        // Verificar horario
        if (promotion.getStartTime() != null && promotion.getEndTime() != null) {
            return isTimeInRange(currentTime, promotion.getStartTime(), promotion.getEndTime());
        }

        return true;
    }

    private static boolean isTimeInRange(LocalTime current, LocalTime start, LocalTime end) {
        if (start.isBefore(end)) {
            // Rango normal: ej. 14:00 - 16:00
            return !current.isBefore(start) && !current.isAfter(end);
        } else {
            // Rango que cruza medianoche: ej. 22:00 - 02:00
            return !current.isBefore(start) || !current.isAfter(end);
        }
    }
}
