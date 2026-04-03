package ar.uba.fi.ingsoft1.product_example.orders.dto;

import ar.uba.fi.ingsoft1.product_example.orders.EstadoPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoResponse {
    
    private Long id;
    private Long usuarioId;
    private String nombreUsuario;
    private EstadoPedido estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private BigDecimal total;
    private String observaciones;
    private String usuarioModificacion;
    private List<DetallePedidoResponse> detalles;
    private Long appliedPromotionId;
    private String appliedPromotionName;
    private BigDecimal discountAmount;
    private BigDecimal subtotalBeforeDiscount;

    public PedidoResponse() {}

    public PedidoResponse(Long id, Long usuarioId, String nombreUsuario, EstadoPedido estado,
                         LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion, 
                         BigDecimal total, String observaciones, String usuarioModificacion,
                         List<DetallePedidoResponse> detalles, Long appliedPromotionId,
                         String appliedPromotionName, BigDecimal discountAmount,
                         BigDecimal subtotalBeforeDiscount) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.nombreUsuario = nombreUsuario;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
        this.total = total;
        this.observaciones = observaciones;
        this.usuarioModificacion = usuarioModificacion;
        this.detalles = detalles;
        this.appliedPromotionId = appliedPromotionId;
        this.appliedPromotionName = appliedPromotionName;
        this.discountAmount = discountAmount;
        this.subtotalBeforeDiscount = subtotalBeforeDiscount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
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

    public List<DetallePedidoResponse> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedidoResponse> detalles) {
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