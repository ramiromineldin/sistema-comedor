package ar.uba.fi.ingsoft1.product_example.orders;

import ar.uba.fi.ingsoft1.product_example.orders.dto.CrearPedidoRequest;
import ar.uba.fi.ingsoft1.product_example.orders.dto.DetallePedidoRequest;
import ar.uba.fi.ingsoft1.product_example.orders.dto.PedidoResponse;
import ar.uba.fi.ingsoft1.product_example.products.Product;
import ar.uba.fi.ingsoft1.product_example.products.ProductService;
import ar.uba.fi.ingsoft1.product_example.promotions.Promotion;
import ar.uba.fi.ingsoft1.product_example.promotions.PromotionItem;
import ar.uba.fi.ingsoft1.product_example.promotions.PromotionType;
import ar.uba.fi.ingsoft1.product_example.promotions.PromotionService;
import ar.uba.fi.ingsoft1.product_example.user.Role;
import ar.uba.fi.ingsoft1.product_example.user.User;
import ar.uba.fi.ingsoft1.product_example.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para PedidoService")
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProductService productService;

    @Mock
    private UserService userService;

    @Mock
    private PromotionService promotionService;

    @InjectMocks
    private PedidoService pedidoService;

    private User usuario;
    private Product producto1;
    private Product producto2;

    @BeforeEach
    void setUp() {
        usuario = new User("juanperez", "password", "Juan", "Pérez", "juan@test.com", 25, "M", "Calle 123", Role.USER);

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
    @DisplayName("Debe crear pedido correctamente con stock suficiente")
    void debeCrearPedidoCorrectamenteConStockSuficiente() {
        String username = "juanperez";
        CrearPedidoRequest request = new CrearPedidoRequest(
            Arrays.asList(
                new DetallePedidoRequest(1L, 2),
                new DetallePedidoRequest(2L, 1)
            ),
            "Sin cebolla"
        );
        when(userService.findByUsername(username)).thenReturn(usuario);
        when(productService.findById(1L)).thenReturn(producto1);
        when(productService.findById(2L)).thenReturn(producto2);

        Pedido pedidoGuardado = new Pedido(usuario, "Sin cebolla");
        pedidoGuardado.setId(1L);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoGuardado);

        PedidoResponse response = pedidoService.crearPedido(username, request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(EstadoPedido.PENDIENTE, response.getEstado());

        ArgumentCaptor<Pedido> pedidoCaptor = ArgumentCaptor.forClass(Pedido.class);
        verify(pedidoRepository).save(pedidoCaptor.capture());
        
        Pedido pedidoCapturado = pedidoCaptor.getValue();
        assertEquals(usuario, pedidoCapturado.getUsuario());
        assertEquals(EstadoPedido.PENDIENTE, pedidoCapturado.getEstado());
        assertEquals(2, pedidoCapturado.getDetalles().size());
        assertEquals(new BigDecimal("38.50"), pedidoCapturado.getTotal());
    }

    @Test
    @DisplayName("Debe rechazar pedido automáticamente si no hay stock suficiente")
    void debeRechazarPedidoAutomaticamenteSiNoHayStockSuficiente() {
        String username = "juanperez";
        producto1.setCurrentStock(1);

        CrearPedidoRequest request = new CrearPedidoRequest(
            Arrays.asList(new DetallePedidoRequest(1L, 5)),
            null
        );

        when(userService.findByUsername(username)).thenReturn(usuario);
        when(productService.findById(1L)).thenReturn(producto1);

        Pedido pedidoRechazado = new Pedido(usuario, null);
        pedidoRechazado.setId(1L);
        pedidoRechazado.rechazar("sistema");
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoRechazado);

        PedidoResponse response = pedidoService.crearPedido(username, request);

        assertNotNull(response);
        assertEquals(EstadoPedido.RECHAZADO, response.getEstado());
        
        ArgumentCaptor<Pedido> pedidoCaptor = ArgumentCaptor.forClass(Pedido.class);
        verify(pedidoRepository).save(pedidoCaptor.capture());
        
        Pedido pedidoCapturado = pedidoCaptor.getValue();
        assertEquals(EstadoPedido.RECHAZADO, pedidoCapturado.getEstado());
    }

    @Test
    @DisplayName("Debe lanzar excepción si usuario no existe")
    void debeLanzarExcepcionSiUsuarioNoExiste() {
        String username = "inexistente";
        CrearPedidoRequest request = new CrearPedidoRequest(
            Arrays.asList(new DetallePedidoRequest(1L, 1)),
            null
        );

        when(userService.findByUsername(username))
            .thenThrow(new IllegalArgumentException("Usuario no encontrado"));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> pedidoService.crearPedido(username, request)
        );

        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si producto no existe")
    void debeLanzarExcepcionSiProductoNoExiste() {
        String username = "juanperez";
        CrearPedidoRequest request = new CrearPedidoRequest(
            Arrays.asList(new DetallePedidoRequest(999L, 1)),
            null
        );

        when(userService.findByUsername(username)).thenReturn(usuario);
        when(productService.findById(999L))
            .thenThrow(new IllegalArgumentException("Producto no encontrado"));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> pedidoService.crearPedido(username, request)
        );

        assertEquals("Producto no encontrado", exception.getMessage());
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe cancelar pedido correctamente si está en estado PENDIENTE")
    void debeCancelarPedidoCorrectamenteSiEstaPendiente() {
        Long pedidoId = 1L;
        String username = "juanperez";

        Pedido pedido = new Pedido(usuario, null);
        pedido.setId(pedidoId);

        when(pedidoRepository.findByIdWithDetalles(pedidoId)).thenReturn(Optional.of(pedido));
        when(userService.findByUsername(username)).thenReturn(usuario);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        pedidoService.cancelarPedido(pedidoId, username);

        ArgumentCaptor<Pedido> pedidoCaptor = ArgumentCaptor.forClass(Pedido.class);
        verify(pedidoRepository).save(pedidoCaptor.capture());
        
        Pedido pedidoCapturado = pedidoCaptor.getValue();
        assertEquals(EstadoPedido.CANCELADO, pedidoCapturado.getEstado());
        assertEquals(username, pedidoCapturado.getUsuarioModificacion());
    }

    @Test
    @DisplayName("No debe cancelar pedido si no es el propietario")
    void noDebeCancelarPedidoSiNoEsPropietario() {
        Long pedidoId = 1L;
        String username = "otro";

        User otroUsuario = new User("otro", "password", "Otro", "Usuario", "otro@test.com", 30, "M", "Calle 456", Role.USER);

        Pedido pedido = new Pedido(usuario, null);
        pedido.setId(pedidoId);

        when(pedidoRepository.findByIdWithDetalles(pedidoId)).thenReturn(Optional.of(pedido));
        when(userService.findByUsername(username)).thenReturn(otroUsuario);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> pedidoService.cancelarPedido(pedidoId, username)
        );

        assertTrue(exception.getMessage().contains("No tienes permisos"));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe obtener pedidos activos correctamente")
    void debeObtenerPedidosActivosCorrectamente() {
        List<Pedido> pedidosActivos = Arrays.asList(
            new Pedido(usuario, "Pedido 1"),
            new Pedido(usuario, "Pedido 2")
        );

        when(pedidoRepository.findPedidosActivos()).thenReturn(pedidosActivos);

        List<PedidoResponse> response = pedidoService.obtenerPedidosActivos();

        assertNotNull(response);
        assertEquals(2, response.size());
        verify(pedidoRepository).findPedidosActivos();
    }

    @Test
    @DisplayName("Debe cambiar estado de pedido correctamente")
    void debeCambiarEstadoPedidoCorrectamente() {
        Long pedidoId = 1L;
        EstadoPedido nuevoEstado = EstadoPedido.EN_PREPARACION;
        String emailUsuario = "chef@test.com";

        Pedido pedido = new Pedido(usuario, null);
        pedido.setId(pedidoId);

        when(pedidoRepository.findByIdWithDetalles(pedidoId)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        PedidoResponse response = pedidoService.cambiarEstadoPedido(pedidoId, nuevoEstado, emailUsuario);

        assertNotNull(response);
        assertEquals(EstadoPedido.EN_PREPARACION, response.getEstado());
        
        ArgumentCaptor<Pedido> pedidoCaptor = ArgumentCaptor.forClass(Pedido.class);
        verify(pedidoRepository).save(pedidoCaptor.capture());
        
        Pedido pedidoCapturado = pedidoCaptor.getValue();
        assertEquals(nuevoEstado, pedidoCapturado.getEstado());
        assertEquals(emailUsuario, pedidoCapturado.getUsuarioModificacion());
    }

    @Test
    @DisplayName("Debe rechazar pedido correctamente")
    void debeRechazarPedidoCorrectamente() {
        Long pedidoId = 1L;
        String emailUsuario = "chef@test.com";

        Pedido pedido = new Pedido(usuario, null);
        pedido.setId(pedidoId);
        pedido.setEstado(EstadoPedido.PENDIENTE);

        when(pedidoRepository.findByIdWithDetalles(pedidoId)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        PedidoResponse response = pedidoService.cambiarEstadoPedido(pedidoId, EstadoPedido.RECHAZADO, emailUsuario);

        assertNotNull(response);
        assertEquals(EstadoPedido.RECHAZADO, response.getEstado());
    }

    @Test
    @DisplayName("Debe obtener pedido por ID correctamente")
    void debeObtenerPedidoPorIdCorrectamente() {
        Long pedidoId = 1L;
        Pedido pedido = new Pedido(usuario, "Pedido test");
        pedido.setId(pedidoId);

        when(pedidoRepository.findByIdWithDetalles(pedidoId)).thenReturn(Optional.of(pedido));

        PedidoResponse response = pedidoService.obtenerPedidoPorId(pedidoId);

        assertNotNull(response);
        assertEquals(pedidoId, response.getId());
    }

    @Test
    @DisplayName("Debe lanzar excepción al obtener pedido inexistente")
    void debeLanzarExcepcionAlObtenerPedidoInexistente() {
        Long pedidoId = 999L;

        when(pedidoRepository.findByIdWithDetalles(pedidoId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> pedidoService.obtenerPedidoPorId(pedidoId)
        );

        assertTrue(exception.getMessage().contains("no encontrado"));
    }

    @Test
    @DisplayName("Debe lanzar excepción si request es null")
    void debeLanzarExcepcionSiRequestEsNull() {
        String username = "juanperez";

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> pedidoService.crearPedido(username, null)
        );

        assertTrue(exception.getMessage().contains("al menos un producto"));
    }

    @Test
    @DisplayName("Debe lanzar excepción si username es vacío")
    void debeLanzarExcepcionSiUsernameEsVacio() {
        CrearPedidoRequest request = new CrearPedidoRequest(
            Arrays.asList(new DetallePedidoRequest(1L, 1)),
            null
        );

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> pedidoService.crearPedido("", request)
        );

        assertTrue(exception.getMessage().contains("username"));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando pedidoId es null en cambiarEstado")
    void debeLanzarExcepcionCuandoPedidoIdEsNull() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> pedidoService.cambiarEstadoPedido(null, EstadoPedido.EN_PREPARACION, "chef@test.com")
        );

        assertTrue(exception.getMessage().contains("ID del pedido"));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando nuevoEstado es null")
    void debeLanzarExcepcionCuandoNuevoEstadoEsNull() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> pedidoService.cambiarEstadoPedido(1L, null, "chef@test.com")
        );

        assertTrue(exception.getMessage().contains("nuevo estado"));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando emailUsuario es null")
    void debeLanzarExcepcionCuandoEmailUsuarioEsNull() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> pedidoService.cambiarEstadoPedido(1L, EstadoPedido.EN_PREPARACION, null)
        );

        assertTrue(exception.getMessage().contains("email"));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando emailUsuario es vacío")
    void debeLanzarExcepcionCuandoEmailUsuarioEsVacio() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> pedidoService.cambiarEstadoPedido(1L, EstadoPedido.EN_PREPARACION, "")
        );

        assertTrue(exception.getMessage().contains("email"));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando pedidoId es null en obtenerPedidoPorId")
    void debeLanzarExcepcionCuandoPedidoIdEsNullEnObtener() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> pedidoService.obtenerPedidoPorId(null)
        );

        assertTrue(exception.getMessage().contains("ID del pedido"));
    }

    @Test
    @DisplayName("Debe llamar a save al descontar stock")
    void debeLlamarASaveAlDescontarStock() {
        Pedido pedido = new Pedido(usuario, null);
        pedido.setId(1L);
        pedido.setEstado(EstadoPedido.PENDIENTE);

        DetallePedido detalle = new DetallePedido(producto1, 2, producto1.getPrice());
        detalle.setPedido(pedido);
        pedido.getDetalles().add(detalle);

        when(pedidoRepository.findByIdWithDetalles(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        pedidoService.cambiarEstadoPedido(1L, EstadoPedido.EN_PREPARACION, "chef@test.com");

        verify(productService).save(producto1);
    }

    @Test
    @DisplayName("Debe aplicar descuento por monto mínimo cuando cumple condición")
    void debeAplicarDescuentoPorMontoMinimoCuandoCumpleCondicion() {
        String username = "juanperez";
        
        // Crear promoción de monto mínimo
        Promotion promo = new Promotion();
        promo.setId(1L);
        promo.setName("Descuento $100 por compra mayor a $500");
        promo.setType(PromotionType.AMOUNT_DISCOUNT);
        promo.setMinimumPurchaseAmount(new BigDecimal("500"));
        promo.setDiscountAmount(new BigDecimal("100"));
        promo.setStartDate(LocalDateTime.now().minusDays(1));
        promo.setEndDate(LocalDateTime.now().plusDays(1));
        
        CrearPedidoRequest request = new CrearPedidoRequest(
            Arrays.asList(new DetallePedidoRequest(1L, 40)), // 40 * 15 = 600
            null
        );
        
        when(userService.findByUsername(username)).thenReturn(usuario);
        when(productService.findById(1L)).thenReturn(producto1);
        when(promotionService.findActiveNowEntities(any(LocalDateTime.class)))
            .thenReturn(List.of(promo));
        
        Pedido pedidoGuardado = new Pedido(usuario, null);
        pedidoGuardado.setId(1L);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoGuardado);
        
        PedidoResponse response = pedidoService.crearPedido(username, request);
        
        assertNotNull(response);
        verify(pedidoRepository, atLeastOnce()).save(any(Pedido.class));
    }

    @Test
    @DisplayName("No debe aplicar descuento cuando no cumple monto mínimo")
    void noDebeAplicarDescuentoCuandoNoCumpleMontoMinimo() {
        String username = "juanperez";
        
        Promotion promo = new Promotion();
        promo.setId(1L);
        promo.setName("Descuento $100 por compra mayor a $500");
        promo.setType(PromotionType.AMOUNT_DISCOUNT);
        promo.setMinimumPurchaseAmount(new BigDecimal("500"));
        promo.setDiscountAmount(new BigDecimal("100"));
        promo.setStartDate(LocalDateTime.now().minusDays(1));
        promo.setEndDate(LocalDateTime.now().plusDays(1));
        
        CrearPedidoRequest request = new CrearPedidoRequest(
            Arrays.asList(new DetallePedidoRequest(2L, 10)), // 10 * 8.5 = 85 (no cumple el mínimo)
            null
        );
        
        when(userService.findByUsername(username)).thenReturn(usuario);
        when(productService.findById(2L)).thenReturn(producto2);
        when(promotionService.findActiveNowEntities(any(LocalDateTime.class)))
            .thenReturn(List.of(promo));
        
        Pedido pedidoGuardado = new Pedido(usuario, null);
        pedidoGuardado.setId(1L);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoGuardado);
        
        pedidoService.crearPedido(username, request);
        
        verify(pedidoRepository, atLeastOnce()).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Debe aplicar la mejor promoción cuando hay múltiples")
    void debeAplicarMejorPromocionCuandoHayMultiples() {
        String username = "juanperez";
        
        Promotion promo1 = new Promotion();
        promo1.setId(1L);
        promo1.setName("Descuento $50");
        promo1.setType(PromotionType.AMOUNT_DISCOUNT);
        promo1.setMinimumPurchaseAmount(new BigDecimal("100"));
        promo1.setDiscountAmount(new BigDecimal("50"));
        promo1.setStartDate(LocalDateTime.now().minusDays(1));
        promo1.setEndDate(LocalDateTime.now().plusDays(1));
        
        Promotion promo2 = new Promotion();
        promo2.setId(2L);
        promo2.setName("Descuento $100");
        promo2.setType(PromotionType.AMOUNT_DISCOUNT);
        promo2.setMinimumPurchaseAmount(new BigDecimal("100"));
        promo2.setDiscountAmount(new BigDecimal("100"));
        promo2.setStartDate(LocalDateTime.now().minusDays(1));
        promo2.setEndDate(LocalDateTime.now().plusDays(1));
        
        CrearPedidoRequest request = new CrearPedidoRequest(
            Arrays.asList(new DetallePedidoRequest(1L, 10)), // 10 * 15 = 150
            null
        );
        
        when(userService.findByUsername(username)).thenReturn(usuario);
        when(productService.findById(1L)).thenReturn(producto1);
        when(promotionService.findActiveNowEntities(any(LocalDateTime.class)))
            .thenReturn(List.of(promo1, promo2));
        
        Pedido pedidoGuardado = new Pedido(usuario, null);
        pedidoGuardado.setId(1L);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoGuardado);
        
        pedidoService.crearPedido(username, request);
        
        verify(pedidoRepository, atLeastOnce()).save(any(Pedido.class));
    }

    @Test
    @DisplayName("No debe aplicar descuento cuando best es null")
    void noDebeAplicarDescuentoCuandoBestEsNull() {
        String username = "juanperez";
        
        CrearPedidoRequest request = new CrearPedidoRequest(
            Arrays.asList(new DetallePedidoRequest(1L, 2)),
            null
        );
        
        when(userService.findByUsername(username)).thenReturn(usuario);
        when(productService.findById(1L)).thenReturn(producto1);
        when(promotionService.findActiveNowEntities(any(LocalDateTime.class)))
            .thenReturn(List.of());
        
        Pedido pedidoGuardado = new Pedido(usuario, null);
        pedidoGuardado.setId(1L);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoGuardado);
        
        pedidoService.crearPedido(username, request);
        
        verify(pedidoRepository, atLeastOnce()).save(any(Pedido.class));
    }

    @Test
    @DisplayName("No debe aplicar descuento cuando discountAmount es null")
    void noDebeAplicarDescuentoCuandoDiscountAmountEsNull() {
        String username = "juanperez";
        
        Promotion promo = new Promotion();
        promo.setId(1L);
        promo.setName("Promo sin descuento");
        promo.setType(PromotionType.AMOUNT_DISCOUNT);
        promo.setMinimumPurchaseAmount(new BigDecimal("100"));
        promo.setDiscountAmount(null);
        promo.setStartDate(LocalDateTime.now().minusDays(1));
        promo.setEndDate(LocalDateTime.now().plusDays(1));
        
        CrearPedidoRequest request = new CrearPedidoRequest(
            Arrays.asList(new DetallePedidoRequest(1L, 10)),
            null
        );
        
        when(userService.findByUsername(username)).thenReturn(usuario);
        when(productService.findById(1L)).thenReturn(producto1);
        when(promotionService.findActiveNowEntities(any(LocalDateTime.class)))
            .thenReturn(List.of(promo));
        
        Pedido pedidoGuardado = new Pedido(usuario, null);
        pedidoGuardado.setId(1L);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoGuardado);
        
        pedidoService.crearPedido(username, request);
        
        verify(pedidoRepository, atLeastOnce()).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Debe setear newTotal a ZERO cuando el descuento es mayor al total")
    void debeSetearNewTotalACerooCuandoDescuentoEsMayorAlTotal() {
        String username = "juanperez";
        
        Promotion promo = new Promotion();
        promo.setId(1L);
        promo.setName("Descuento gigante");
        promo.setType(PromotionType.AMOUNT_DISCOUNT);
        promo.setMinimumPurchaseAmount(new BigDecimal("10"));
        promo.setDiscountAmount(new BigDecimal("1000"));
        promo.setStartDate(LocalDateTime.now().minusDays(1));
        promo.setEndDate(LocalDateTime.now().plusDays(1));
        
        CrearPedidoRequest request = new CrearPedidoRequest(
            Arrays.asList(new DetallePedidoRequest(2L, 2)), // 2 * 8.5 = 17
            null
        );
        
        when(userService.findByUsername(username)).thenReturn(usuario);
        when(productService.findById(2L)).thenReturn(producto2);
        when(promotionService.findActiveNowEntities(any(LocalDateTime.class)))
            .thenReturn(List.of(promo));
        
        Pedido pedidoGuardado = new Pedido(usuario, null);
        pedidoGuardado.setId(1L);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoGuardado);
        
        pedidoService.crearPedido(username, request);
        
        verify(pedidoRepository, atLeastOnce()).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Debe crear pedido con promoción y calcular ratio correctamente")
    void debeCrearPedidoConPromocionYCalcularRatio() {
        String username = "juanperez";
        
        Promotion promo = new Promotion();
        promo.setId(1L);
        promo.setName("Promo 2x1");
        promo.setType(PromotionType.BUY_X_PAY_Y);
        promo.setOriginalPrice(new BigDecimal("30.00"));
        promo.setFinalPrice(new BigDecimal("15.00"));
        
        PromotionItem item1 = new PromotionItem();
        item1.setProduct(producto1);
        item1.setOriginalPrice(producto1.getPrice());
        promo.addItem(item1);
        
        PromotionItem item2 = new PromotionItem();
        item2.setProduct(producto2);
        item2.setOriginalPrice(producto2.getPrice());
        promo.addItem(item2);
        
        DetallePedidoRequest detalle = new DetallePedidoRequest();
        detalle.setPromotionId(1L);
        detalle.setCantidad(1);
        
        CrearPedidoRequest request = new CrearPedidoRequest(
            Arrays.asList(detalle),
            null
        );
        
        when(userService.findByUsername(username)).thenReturn(usuario);
        when(promotionService.findPromotion(1L)).thenReturn(promo);
        
        Pedido pedidoGuardado = new Pedido(usuario, null);
        pedidoGuardado.setId(1L);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoGuardado);
        
        pedidoService.crearPedido(username, request);
        
        verify(pedidoRepository, atLeastOnce()).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Debe crear pedido con promoción sin precio original (gratis)")
    void debeCrearPedidoConPromocionSinPrecioOriginal() {
        String username = "juanperez";
        
        Promotion promo = new Promotion();
        promo.setId(1L);
        promo.setName("Promo gratis");
        promo.setType(PromotionType.PERCENTAGE_DISCOUNT);
        promo.setOriginalPrice(BigDecimal.ZERO);
        promo.setFinalPrice(BigDecimal.ZERO);
        
        PromotionItem item1 = new PromotionItem();
        item1.setProduct(producto1);
        item1.setOriginalPrice(producto1.getPrice());
        promo.addItem(item1);
        
        DetallePedidoRequest detalle = new DetallePedidoRequest();
        detalle.setPromotionId(1L);
        detalle.setCantidad(1);
        
        CrearPedidoRequest request = new CrearPedidoRequest(
            Arrays.asList(detalle),
            null
        );
        
        when(userService.findByUsername(username)).thenReturn(usuario);
        when(promotionService.findPromotion(1L)).thenReturn(promo);
        
        Pedido pedidoGuardado = new Pedido(usuario, null);
        pedidoGuardado.setId(1L);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoGuardado);
        
        pedidoService.crearPedido(username, request);
        
        verify(pedidoRepository, atLeastOnce()).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando pedido no puede cancelarse")
    void debeLanzarExcepcionCuandoPedidoNoPuedeCancelarse() {
        Long pedidoId = 1L;
        String username = "juanperez";
        
        Pedido pedido = new Pedido(usuario, null);
        pedido.setId(pedidoId);
        pedido.setEstado(EstadoPedido.EN_PREPARACION); // Estado que no puede cancelarse
        
        when(pedidoRepository.findByIdWithDetalles(pedidoId)).thenReturn(Optional.of(pedido));
        when(userService.findByUsername(username)).thenReturn(usuario);
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> pedidoService.cancelarPedido(pedidoId, username)
        );
        
        assertTrue(exception.getMessage().contains("no se puede cancelar"));
    }

    @Test
    @DisplayName("Debe obtener historial de pedidos")
    void debeObtenerHistorialPedidos() {
        List<Pedido> historial = Arrays.asList(
            new Pedido(usuario, "Pedido 1"),
            new Pedido(usuario, "Pedido 2")
        );
        
        when(pedidoRepository.findHistorial()).thenReturn(historial);
        
        List<PedidoResponse> response = pedidoService.obtenerHistorialPedidos();
        
        assertNotNull(response);
        assertEquals(2, response.size());
        verify(pedidoRepository).findHistorial();
    }

    @Test
    @DisplayName("Debe obtener pedidos por usuario")
    void debeObtenerPedidosPorUsuario() {
        String username = "juanperez";
        List<Pedido> pedidos = Arrays.asList(
            new Pedido(usuario, "Pedido 1"),
            new Pedido(usuario, "Pedido 2")
        );
        
        when(userService.findByUsername(username)).thenReturn(usuario);
        when(pedidoRepository.findByUsuarioOrderByFechaCreacionDesc(usuario)).thenReturn(pedidos);
        
        List<PedidoResponse> response = pedidoService.obtenerPedidosPorUsuario(username);
        
        assertNotNull(response);
        assertEquals(2, response.size());
        verify(pedidoRepository).findByUsuarioOrderByFechaCreacionDesc(usuario);
    }

    @Test
    @DisplayName("Debe lanzar excepción al cambiar estado con stock insuficiente")
    void debeLanzarExcepcionAlCambiarEstadoConStockInsuficiente() {
        Long pedidoId = 1L;
        
        producto1.setCurrentStock(1); // Stock menor a lo solicitado
        
        Pedido pedido = new Pedido(usuario, null);
        pedido.setId(pedidoId);
        pedido.setEstado(EstadoPedido.PENDIENTE);
        
        DetallePedido detalle = new DetallePedido(producto1, 5, producto1.getPrice()); // Solicita 5 pero solo hay 1
        detalle.setPedido(pedido);
        pedido.getDetalles().add(detalle);
        
        when(pedidoRepository.findByIdWithDetalles(pedidoId)).thenReturn(Optional.of(pedido));
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> pedidoService.cambiarEstadoPedido(pedidoId, EstadoPedido.EN_PREPARACION, "chef@test.com")
        );
        
        assertTrue(exception.getMessage().contains("Stock insuficiente"));
    }
}
