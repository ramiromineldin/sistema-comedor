package ar.uba.fi.ingsoft1.product_example.orders;

import ar.uba.fi.ingsoft1.product_example.products.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
@Table(name = "detalle_pedido")
public class DetallePedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull(message = "El producto es obligatorio")
    private Product product;

    @Column(nullable = false)
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "El precio unitario es obligatorio")
    private BigDecimal precioUnitario;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "El subtotal es obligatorio")
    private BigDecimal subtotal;

    public DetallePedido() {}

    public DetallePedido(Product product, Integer cantidad) {
        this.product = product;
        this.cantidad = cantidad;
        this.precioUnitario = product.getPrice();
        this.subtotal = this.precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    public DetallePedido(Product product, Integer cantidad, BigDecimal precioUnitario) {
        this.product = product;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = this.precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }


    public boolean tieneStockSuficiente() {

        return product.getCurrentStock() >= cantidad;
    }
}