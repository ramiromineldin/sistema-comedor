package ar.uba.fi.ingsoft1.product_example.orders;

public enum EstadoPedido {
    PENDIENTE("Pendiente"),
    EN_PREPARACION("En preparación"),
    PREPARADO("Preparado"),
    ENTREGADO("Entregado"),
    CANCELADO("Cancelado"),
    RECHAZADO("Rechazado");

    private final String descripcion;

    EstadoPedido(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean puedeTransicionarA(EstadoPedido nuevoEstado) {
        return switch (this) {
            case PENDIENTE -> nuevoEstado == EN_PREPARACION || 
                            nuevoEstado == CANCELADO || 
                            nuevoEstado == RECHAZADO;
            case EN_PREPARACION -> nuevoEstado == PREPARADO;
            case PREPARADO -> nuevoEstado == ENTREGADO;
            case ENTREGADO, CANCELADO, RECHAZADO -> false; 
        };
    }

    public boolean esEstadoFinal() {
        return this == ENTREGADO || this == CANCELADO || this == RECHAZADO;
    }

    public boolean permiteModificacion() {
        return this == PENDIENTE;
    }
}