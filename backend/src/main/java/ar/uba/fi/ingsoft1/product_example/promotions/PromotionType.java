package ar.uba.fi.ingsoft1.product_example.promotions;


import ar.uba.fi.ingsoft1.product_example.promotions.strategy.*;

public enum PromotionType {
    PERCENTAGE_DISCOUNT(new PercentageDiscountStrategy()),       // Descuento porcentual temporal (ej: 20% off este mes)
    RECURRING_DAY_DISCOUNT(new RecurringDayDiscountStrategy()),  // Descuento porcentual en día específico (ej: 20% los martes)
    AMOUNT_DISCOUNT(new AmountDiscountStrategy()),               // Descuento fijo sobre el total (ej: $5000 off en compras >$30000)
    BUY_X_PAY_Y(new BuyXPayYStrategy()),                         // Compra X paga Y (ej: 3x2, 2x1, etc)
    TIME_RESTRICTED_BUY_X_PAY_Y(new TimeRestrictedBuyXPayYStrategy()); // Compra X paga Y con restricción horaria (ej: 2x1 después de las 18hs)

    private final PromotionStrategy strategy;

    PromotionType(PromotionStrategy strategy) {
        this.strategy = strategy;
    }

    public PromotionStrategy getStrategy() {
        return strategy;
    }
}
