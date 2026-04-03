package ar.uba.fi.ingsoft1.product_example.orders;

import ar.uba.fi.ingsoft1.product_example.orders.dto.CrearPedidoRequest;
import ar.uba.fi.ingsoft1.product_example.orders.dto.PedidoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoRestController {

    private final PedidoService pedidoService;

    @Autowired
    public PedidoRestController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> crearPedido(
            @Valid @RequestBody CrearPedidoRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        PedidoResponse pedido = pedidoService.crearPedido(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }

    @GetMapping("/mis-pedidos")
    public ResponseEntity<List<PedidoResponse>> obtenerMisPedidos(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        List<PedidoResponse> pedidos = pedidoService.obtenerPedidosPorUsuario(userDetails.getUsername());
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> obtenerPedido(@PathVariable Long id) {
        PedidoResponse pedido = pedidoService.obtenerPedidoPorId(id);
        return ResponseEntity.ok(pedido);
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarPedido(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        pedidoService.cancelarPedido(id, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    // Endpoints para el personal del comedor

    @GetMapping("/activos")
    public ResponseEntity<List<PedidoResponse>> obtenerPedidosActivos() {
        List<PedidoResponse> pedidos = pedidoService.obtenerPedidosActivos();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/historial")
    public ResponseEntity<List<PedidoResponse>> obtenerHistorialPedidos() {
        List<PedidoResponse> pedidos = pedidoService.obtenerHistorialPedidos();
        return ResponseEntity.ok(pedidos);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<PedidoResponse> cambiarEstadoPedido(
            @PathVariable Long id,
            @RequestParam EstadoPedido nuevoEstado,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        PedidoResponse pedido = pedidoService.cambiarEstadoPedido(id, nuevoEstado, userDetails.getUsername());
        return ResponseEntity.ok(pedido);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}