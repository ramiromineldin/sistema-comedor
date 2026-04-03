package ar.uba.fi.ingsoft1.product_example.orders;

import ar.uba.fi.ingsoft1.product_example.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "El usuario es obligatorio")
    private User usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "El estado es obligatorio")
    private EstadoPedido estado;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "El total es obligatorio")
    private BigDecimal total;

    @Column(name = "observaciones")
    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    private String observaciones;

    @Column(name = "usuario_modificacion")
    private String usuarioModificacion;

    @Column(name = "promotion_id")
    private Long appliedPromotionId;

    @Column(name = "promotion_name")
    private String appliedPromotionName;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "subtotal_before_discount", precision = 10, scale = 2)
    private BigDecimal subtotalBeforeDiscount;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetallePedido> detalles = new ArrayList<>();

    public Pedido() {}

    public Pedido(User usuario, String observaciones) {
        this.usuario = usuario;
        this.estado = EstadoPedido.PENDIENTE;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        this.total = BigDecimal.ZERO;
        this.observaciones = observaciones;
    }

    public void agregarDetalle(DetallePedido detalle) {
        detalle.setPedido(this);
        this.detalles.add(detalle);
        recalcularTotal();
    }

    public void removerDetalle(DetallePedido detalle) {
        this.detalles.remove(detalle);
        detalle.setPedido(null);
        recalcularTotal();
    }

    private void recalcularTotal() {
        this.total = detalles.stream()
            .map(DetallePedido::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean puedeTransicionarA(EstadoPedido nuevoEstado) {
        return this.estado.puedeTransicionarA(nuevoEstado);
    }

    public void cambiarEstado(EstadoPedido nuevoEstado, String usuarioModificacion) {
        if (!puedeTransicionarA(nuevoEstado)) {
            throw new IllegalArgumentException(
                String.format("No se puede cambiar de %s a %s", 
                    this.estado.getDescripcion(), nuevoEstado.getDescripcion())
            );
        }
        
        this.estado = nuevoEstado;
        this.usuarioModificacion = usuarioModificacion;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public boolean puedeCancelarse() {
        return estado.permiteModificacion();
    }

    public boolean tieneStockSuficiente() {
        return detalles.stream().allMatch(DetallePedido::tieneStockSuficiente);
    }

    public void cancelar(String usuarioModificacion) {
        cambiarEstado(EstadoPedido.CANCELADO, usuarioModificacion);
    }

    public void rechazar(String usuarioModificacion) {
        cambiarEstado(EstadoPedido.RECHAZADO, usuarioModificacion);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getUsuarioModificacion() {
        return usuarioModificacion;
    }

    public void setUsuarioModificacion(String usuarioModificacion) {
        this.usuarioModificacion = usuarioModificacion;
    }

    public List<DetallePedido> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedido> detalles) {
        this.detalles = detalles;
    }

    public Long getAppliedPromotionId() {
        return appliedPromotionId;
    }

    public void setAppliedPromotionId(Long appliedPromotionId) {
        this.appliedPromotionId = appliedPromotionId;
    }

    public String getAppliedPromotionName() {
        return appliedPromotionName;
    }

    public void setAppliedPromotionName(String appliedPromotionName) {
        this.appliedPromotionName = appliedPromotionName;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getSubtotalBeforeDiscount() {
        return subtotalBeforeDiscount;
    }

    public void setSubtotalBeforeDiscount(BigDecimal subtotalBeforeDiscount) {
        this.subtotalBeforeDiscount = subtotalBeforeDiscount;
    }
}