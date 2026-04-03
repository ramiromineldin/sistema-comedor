package ar.uba.fi.ingsoft1.product_example.promotions.strategy;

import ar.uba.fi.ingsoft1.product_example.promotions.Promotion;
import ar.uba.fi.ingsoft1.product_example.promotions.dto.PromotionCreateDTO;

import java.math.BigDecimal;
import java.time.LocalTime;

public class TimeRestrictedBuyXPayYStrategy implements PromotionStrategy {
    
    @Override
    public void configure(Promotion promotion, PromotionCreateDTO request) {
        promotion.setBuyQuantity(request.buyQuantity());
        promotion.setPayQuantity(request.payQuantity());
        promotion.setStartTime(request.startTime());
        promotion.setEndTime(request.endTime());
    }

    @Override
    public void validate(PromotionCreateDTO request) {
        if (request.buyQuantity() == null || request.buyQuantity() <= 0) {
            throw new IllegalArgumentException("La cantidad a llevar debe ser mayor a 0");
        }
        if (request.payQuantity() == null || request.payQuantity() <= 0) {
            throw new IllegalArgumentException("La cantidad a pagar debe ser mayor a 0");
        }
        if (request.payQuantity() >= request.buyQuantity()) {
            throw new IllegalArgumentException("La cantidad a pagar debe ser menor a la cantidad a llevar");
        }
        if (request.startTime() == null) {
            throw new IllegalArgumentException("La hora de inicio es requerida para promociones con restricción horaria");
        }
        if (request.endTime() == null) {
            throw new IllegalArgumentException("La hora de fin es requerida para promociones con restricción horaria");
        }
    }

    @Override
    public BigDecimal calculateFinalPrice(Promotion promotion, BigDecimal originalPrice) {
        if (originalPrice == null || originalPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        
        Integer pay = promotion.getPayQuantity();
        if (pay == null || pay <= 0) {
            return originalPrice;
        }

        
        return originalPrice.multiply(BigDecimal.valueOf(pay))
        .divide(BigDecimal.valueOf(promotion.getBuyQuantity()), 2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Método protegido para obtener la hora actual.
     * Permite mockear en tests.
     */
    protected LocalTime getCurrentTime() {
        return LocalTime.now();
    }

    /**
     * Verifica si una hora está dentro de un rango.
     * Maneja correctamente rangos que cruzan medianoche (ej: 22:00 - 02:00)
     */
    private boolean isTimeInRange(LocalTime time, LocalTime start, LocalTime end) {
        if (start.isBefore(end)) {
            
            return !time.isBefore(start) && !time.isAfter(end);
        } else {
            
            return !time.isBefore(start) || !time.isAfter(end);
        }
    }
}
