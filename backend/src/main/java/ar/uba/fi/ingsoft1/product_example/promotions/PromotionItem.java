package ar.uba.fi.ingsoft1.product_example.promotions;

import ar.uba.fi.ingsoft1.product_example.products.Product;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "promotion_items")
@Data
@NoArgsConstructor
public class PromotionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(precision = 10, scale = 2)
    private BigDecimal originalPrice;

}
