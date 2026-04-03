package ar.uba.fi.ingsoft1.product_example.alimentos;

import java.math.BigDecimal;


public interface ElementoMenu {
    Long getId();
    String getName();
    String getDescription();
    BigDecimal getPrice();
    boolean isAvailable();
}