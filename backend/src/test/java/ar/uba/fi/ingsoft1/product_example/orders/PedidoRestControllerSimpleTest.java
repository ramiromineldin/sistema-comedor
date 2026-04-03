package ar.uba.fi.ingsoft1.product_example.orders;

import ar.uba.fi.ingsoft1.product_example.orders.dto.CrearPedidoRequest;
import ar.uba.fi.ingsoft1.product_example.orders.dto.DetallePedidoRequest;
import ar.uba.fi.ingsoft1.product_example.orders.dto.DetallePedidoResponse;
import ar.uba.fi.ingsoft1.product_example.orders.dto.PedidoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios simples para PedidoRestController")
class PedidoRestControllerSimpleTest {

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private PedidoRestController controller;

    private UserDetails userDetails;
    private CrearPedidoRequest crearPedidoRequest;
    private PedidoResponse pedidoResponse;

    @BeforeEach
    void setUp() {
        userDetails = User.builder()
                .username("juan@test.com")
                .password("password")
                .authorities("ROLE_ESTUDIANTE")
                .build();

        crearPedidoRequest = new CrearPedidoRequest(
                Arrays.asList(new DetallePedidoRequest(1L, 2)),
                "Sin cebolla"
        );

        pedidoResponse = new PedidoResponse(
                1L,
                1L,
                "Juan Pérez",
                EstadoPedido.PENDIENTE,
                LocalDateTime.now(),
                LocalDateTime.now(),
                new BigDecimal("30.00"),
                "Sin cebolla",
                null,
                Arrays.asList(new DetallePedidoResponse(1L, 1L, "Hamburguesa", 2, new BigDecimal("15.00"), new BigDecimal("30.00"))),
                null,
                null,
                BigDecimal.ZERO,
                null
        );
    }

    @Test
    @DisplayName("Debe crear pedido correctamente")
    void debeCrearPedidoCorrectamente() {
        when(pedidoService.crearPedido(eq("juan@test.com"), any(CrearPedidoRequest.class)))
                .thenReturn(pedidoResponse);

        ResponseEntity<PedidoResponse> response = controller.crearPedido(crearPedidoRequest, userDetails);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(pedidoResponse, response.getBody());
        verify(pedidoService).crearPedido("juan@test.com", crearPedidoRequest);
    }

    @Test
    @DisplayName("Debe obtener pedidos del usuario")
    void debeObtenerPedidosDelUsuario() {
        List<PedidoResponse> pedidos = Arrays.asList(pedidoResponse);
        when(pedidoService.obtenerPedidosPorUsuario("juan@test.com")).thenReturn(pedidos);

        ResponseEntity<List<PedidoResponse>> response = controller.obtenerMisPedidos(userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pedidos, response.getBody());
        verify(pedidoService).obtenerPedidosPorUsuario("juan@test.com");
    }

    @Test
    @DisplayName("Debe obtener pedido por ID")
    void debeObtenerPedidoPorId() {
        Long pedidoId = 1L;
        when(pedidoService.obtenerPedidoPorId(pedidoId)).thenReturn(pedidoResponse);

        ResponseEntity<PedidoResponse> response = controller.obtenerPedido(pedidoId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pedidoResponse, response.getBody());
        verify(pedidoService).obtenerPedidoPorId(pedidoId);
    }

    @Test
    @DisplayName("Debe cancelar pedido correctamente")
    void debeCancelarPedidoCorrectamente() {
        Long pedidoId = 1L;
        doNothing().when(pedidoService).cancelarPedido(pedidoId, "juan@test.com");

        ResponseEntity<Void> response = controller.cancelarPedido(pedidoId, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(pedidoService).cancelarPedido(pedidoId, "juan@test.com");
    }

    @Test
    @DisplayName("Debe obtener pedidos activos")
    void debeObtenerPedidosActivos() {
        List<PedidoResponse> pedidos = Arrays.asList(pedidoResponse);
        when(pedidoService.obtenerPedidosActivos()).thenReturn(pedidos);

        ResponseEntity<List<PedidoResponse>> response = controller.obtenerPedidosActivos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pedidos, response.getBody());
        verify(pedidoService).obtenerPedidosActivos();
    }

    @Test
    @DisplayName("Debe cambiar estado de pedido")
    void debeCambiarEstadoPedido() {
        // Given
        Long pedidoId = 1L;
        EstadoPedido nuevoEstado = EstadoPedido.EN_PREPARACION;
        PedidoResponse pedidoActualizado = new PedidoResponse(
                pedidoId, 1L, "Juan Pérez", nuevoEstado,
                LocalDateTime.now(), LocalDateTime.now(), 
                new BigDecimal("30.00"), null, "chef@test.com", 
                Collections.emptyList(),
                null, null, BigDecimal.ZERO, null
        );
        
        when(pedidoService.cambiarEstadoPedido(pedidoId, nuevoEstado, "chef@test.com"))
                .thenReturn(pedidoActualizado);

        UserDetails chef = User.builder()
                .username("chef@test.com")
                .password("password")
                .authorities("ROLE_PERSONAL_COMEDOR")
                .build();

        ResponseEntity<PedidoResponse> response = controller.cambiarEstadoPedido(pedidoId, nuevoEstado, chef);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pedidoActualizado, response.getBody());
        verify(pedidoService).cambiarEstadoPedido(pedidoId, nuevoEstado, "chef@test.com");
    }

    @Test
    @DisplayName("Debe manejar excepción IllegalArgumentException")
    void debeManexarExcepcionIllegalArgumentException() {
        String mensajeError = "Pedido no encontrado";
        IllegalArgumentException exception = new IllegalArgumentException(mensajeError);

        ResponseEntity<String> response = controller.handleIllegalArgumentException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(mensajeError, response.getBody());
    }

    @Test
    @DisplayName("Debe obtener historial de pedidos")
    void debeObtenerHistorialPedidos() {
        List<PedidoResponse> historial = Arrays.asList(pedidoResponse);
        when(pedidoService.obtenerHistorialPedidos()).thenReturn(historial);

        ResponseEntity<List<PedidoResponse>> response = controller.obtenerHistorialPedidos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(historial, response.getBody());
        verify(pedidoService).obtenerHistorialPedidos();
    }
}