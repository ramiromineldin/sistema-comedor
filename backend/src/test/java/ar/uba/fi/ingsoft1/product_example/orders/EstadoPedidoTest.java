package ar.uba.fi.ingsoft1.product_example.orders;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para EstadoPedido")
class EstadoPedidoTest {

    @Test
    @DisplayName("Debe permitir transición de PENDIENTE a EN_PREPARACION")
    void debePermitirTransicionDePendienteAEnPreparacion() {
        EstadoPedido estadoActual = EstadoPedido.PENDIENTE;
        EstadoPedido nuevoEstado = EstadoPedido.EN_PREPARACION;
        boolean puedeTransicionar = estadoActual.puedeTransicionarA(nuevoEstado);
        assertTrue(puedeTransicionar);
    }

    @Test
    @DisplayName("Debe permitir transición de PENDIENTE a CANCELADO")
    void debePermitirTransicionDePendienteACancelado() {
        EstadoPedido estadoActual = EstadoPedido.PENDIENTE;
        EstadoPedido nuevoEstado = EstadoPedido.CANCELADO;
        boolean puedeTransicionar = estadoActual.puedeTransicionarA(nuevoEstado);
        assertTrue(puedeTransicionar);
    }

    @Test
    @DisplayName("Debe permitir transición de PENDIENTE a RECHAZADO")
    void debePermitirTransicionDePendienteARechazado() {
        EstadoPedido estadoActual = EstadoPedido.PENDIENTE;
        EstadoPedido nuevoEstado = EstadoPedido.RECHAZADO;
        boolean puedeTransicionar = estadoActual.puedeTransicionarA(nuevoEstado);
        assertTrue(puedeTransicionar);
    }

    @Test
    @DisplayName("Debe permitir transición de EN_PREPARACION a PREPARADO")
    void debePermitirTransicionDeEnPreparacionAPreparado() {
        EstadoPedido estadoActual = EstadoPedido.EN_PREPARACION;
        EstadoPedido nuevoEstado = EstadoPedido.PREPARADO;
        boolean puedeTransicionar = estadoActual.puedeTransicionarA(nuevoEstado);
        assertTrue(puedeTransicionar);
    }

    @Test
    @DisplayName("Debe permitir transición de PREPARADO a ENTREGADO")
    void debePermitirTransicionDePreparadoAEntregado() {
        EstadoPedido estadoActual = EstadoPedido.PREPARADO;
        EstadoPedido nuevoEstado = EstadoPedido.ENTREGADO;
        boolean puedeTransicionar = estadoActual.puedeTransicionarA(nuevoEstado);
        assertTrue(puedeTransicionar);
    }

    @Test
    @DisplayName("No debe permitir transición de EN_PREPARACION a CANCELADO")
    void noDebePermitirTransicionDeEnPreparacionACancelado() {
        EstadoPedido estadoActual = EstadoPedido.EN_PREPARACION;
        EstadoPedido nuevoEstado = EstadoPedido.CANCELADO;
        boolean puedeTransicionar = estadoActual.puedeTransicionarA(nuevoEstado);
        assertFalse(puedeTransicionar);
    }

    @Test
    @DisplayName("No debe permitir transiciones desde estados finales")
    void noDebePermitirTransicionesDesdeEstadosFinales() {
        EstadoPedido[] estadosFinales = {
            EstadoPedido.ENTREGADO, 
            EstadoPedido.CANCELADO, 
            EstadoPedido.RECHAZADO
        };
        
        for (EstadoPedido estadoFinal : estadosFinales) {
            assertFalse(estadoFinal.puedeTransicionarA(EstadoPedido.PENDIENTE));
            assertFalse(estadoFinal.puedeTransicionarA(EstadoPedido.EN_PREPARACION));
            assertFalse(estadoFinal.puedeTransicionarA(EstadoPedido.PREPARADO));
            assertTrue(estadoFinal.esEstadoFinal());
        }
    }

    @Test
    @DisplayName("Solo PENDIENTE debe permitir modificaciones")
    void soloEstadoPendienteDebePermitirModificaciones() {
        assertTrue(EstadoPedido.PENDIENTE.permiteModificacion());
        assertFalse(EstadoPedido.EN_PREPARACION.permiteModificacion());
        assertFalse(EstadoPedido.PREPARADO.permiteModificacion());
        assertFalse(EstadoPedido.ENTREGADO.permiteModificacion());
        assertFalse(EstadoPedido.CANCELADO.permiteModificacion());
        assertFalse(EstadoPedido.RECHAZADO.permiteModificacion());
    }
}