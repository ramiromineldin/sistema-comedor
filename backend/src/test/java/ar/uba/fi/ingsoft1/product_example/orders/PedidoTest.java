package ar.uba.fi.ingsoft1.product_example.orders;

import ar.uba.fi.ingsoft1.product_example.products.Product;
import ar.uba.fi.ingsoft1.product_example.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para Pedido")
class PedidoTest {

    private User usuario;
    private Product producto1;
    private Product producto2;

    @BeforeEach
    void setUp() {
        usuario = new User();
        usuario.setNombre("Juan");
        usuario.setApellido("Pérez");
        usuario.setEmail("juan@test.com");

        producto1 = new Product();
        producto1.setId(1L);
        producto1.setName("Hamburguesa");
        producto1.setPrice(new BigDecimal("15.00"));
        producto1.setCurrentStock(50);

        producto2 = new Product();
        producto2.setId(2L);
        producto2.setName("Papas fritas");
        producto2.setPrice(new BigDecimal("8.50"));
        producto2.setCurrentStock(30);
    }

    @Test
    @DisplayName("Debe crear un pedido en estado PENDIENTE")
    void debeCrearPedidoEnEstadoPendiente() {
        Pedido pedido = new Pedido(usuario, "Sin cebolla");

        assertEquals(EstadoPedido.PENDIENTE, pedido.getEstado());
        assertEquals(usuario, pedido.getUsuario());
        assertEquals("Sin cebolla", pedido.getObservaciones());
        assertEquals(BigDecimal.ZERO, pedido.getTotal());
        assertNotNull(pedido.getFechaCreacion());
        assertNotNull(pedido.getFechaActualizacion());
    }

    @Test
    @DisplayName("Debe agregar detalles y recalcular el total")
    void debeAgregarDetallesYRecalcularTotal() {
        Pedido pedido = new Pedido(usuario, null);
        DetallePedido detalle1 = new DetallePedido(producto1, 2);
        DetallePedido detalle2 = new DetallePedido(producto2, 1);
        pedido.agregarDetalle(detalle1);
        pedido.agregarDetalle(detalle2);
        assertEquals(2, pedido.getDetalles().size());
        assertEquals(new BigDecimal("38.50"), pedido.getTotal());
        assertEquals(pedido, detalle1.getPedido());
        assertEquals(pedido, detalle2.getPedido());
    }

    @Test
    @DisplayName("Debe remover detalles y recalcular el total")
    void debeRemoverDetallesYRecalcularTotal() {
        Pedido pedido = new Pedido(usuario, null);
        DetallePedido detalle1 = new DetallePedido(producto1, 2);
        DetallePedido detalle2 = new DetallePedido(producto2, 1);
        pedido.agregarDetalle(detalle1);
        pedido.agregarDetalle(detalle2);
        pedido.removerDetalle(detalle1);
        assertEquals(1, pedido.getDetalles().size());
        assertEquals(new BigDecimal("8.50"), pedido.getTotal());
        assertNull(detalle1.getPedido());
    }

    @Test
    @DisplayName("Debe cambiar estado correctamente")
    void debeCambiarEstadoCorrectamente() {
        Pedido pedido = new Pedido(usuario, null);
        String usuarioModificacion = "chef@test.com";
        pedido.cambiarEstado(EstadoPedido.EN_PREPARACION, usuarioModificacion);
        assertEquals(EstadoPedido.EN_PREPARACION, pedido.getEstado());
        assertEquals(usuarioModificacion, pedido.getUsuarioModificacion());
        assertNotNull(pedido.getFechaActualizacion());
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar transición inválida")
    void debeLanzarExcepcionAlIntentarTransicionInvalida() {
        Pedido pedido = new Pedido(usuario, null);
        pedido.cambiarEstado(EstadoPedido.EN_PREPARACION, "chef@test.com");
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> pedido.cambiarEstado(EstadoPedido.CANCELADO, "user@test.com")
        );
        assertTrue(exception.getMessage().contains("No se puede cambiar de"));
    }
    @Test
    @DisplayName("Debe cancelar pedido solo si está en estado PENDIENTE")
    void debeCancelarPedidoSoloSiEstaPendiente() {
        Pedido pedido = new Pedido(usuario, null);
        assertTrue(pedido.puedeCancelarse());
        pedido.cancelar("user@test.com");
        assertEquals(EstadoPedido.CANCELADO, pedido.getEstado());
        assertFalse(pedido.puedeCancelarse());
    }

    @Test
    @DisplayName("No debe poder cancelar pedido en preparación")
    void noDebePoderCancelarPedidoEnPreparacion() {
        Pedido pedido = new Pedido(usuario, null);
        pedido.cambiarEstado(EstadoPedido.EN_PREPARACION, "chef@test.com");
        assertFalse(pedido.puedeCancelarse());
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> pedido.cancelar("user@test.com")
        );
        
        assertTrue(exception.getMessage().contains("No se puede cambiar de"));
    }

    @Test
    @DisplayName("Debe verificar stock suficiente")
    void debeVerificarStockSuficiente() {
        Pedido pedido = new Pedido(usuario, null);
        DetallePedido detalle1 = new DetallePedido(producto1, 10); 
        DetallePedido detalle2 = new DetallePedido(producto2, 5); 

        pedido.agregarDetalle(detalle1);
        pedido.agregarDetalle(detalle2);
        assertTrue(pedido.tieneStockSuficiente());
    }

    @Test
    @DisplayName("Debe detectar stock insuficiente")
    void debeDetectarStockInsuficiente() {
        Pedido pedido = new Pedido(usuario, null);
        DetallePedido detalle1 = new DetallePedido(producto1, 10); 
        DetallePedido detalle2 = new DetallePedido(producto2, 50); 
        pedido.agregarDetalle(detalle1);
        pedido.agregarDetalle(detalle2);
        assertFalse(pedido.tieneStockSuficiente());
    }

    @Test
    @DisplayName("Debe rechazar pedido")
    void debeRechazarPedido() {
        Pedido pedido = new Pedido(usuario, null);
        String usuarioModificacion = "sistema@test.com";
        pedido.rechazar(usuarioModificacion);
        assertEquals(EstadoPedido.RECHAZADO, pedido.getEstado());
        assertEquals(usuarioModificacion, pedido.getUsuarioModificacion());
    }
}