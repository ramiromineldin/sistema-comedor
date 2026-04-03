package ar.uba.fi.ingsoft1.product_example.orders;

import ar.uba.fi.ingsoft1.product_example.orders.dto.*;
import ar.uba.fi.ingsoft1.product_example.products.Product;
import ar.uba.fi.ingsoft1.product_example.products.ProductService;
import ar.uba.fi.ingsoft1.product_example.promotions.Promotion;
import ar.uba.fi.ingsoft1.product_example.promotions.PromotionService;
import ar.uba.fi.ingsoft1.product_example.promotions.PromotionType;
import ar.uba.fi.ingsoft1.product_example.user.User;
import ar.uba.fi.ingsoft1.product_example.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductService productService;
    private final UserService userService;
    private final PromotionService promotionService;

    @Autowired
    public PedidoService(PedidoRepository pedidoRepository, 
                        ProductService productService,
                        UserService userService,
                         PromotionService promotionService) {
        this.pedidoRepository = pedidoRepository;
        this.productService = productService;
        this.userService = userService;
        this.promotionService = promotionService;
    }

    public PedidoResponse crearPedido(String username, CrearPedidoRequest request) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El username del usuario es obligatorio");
        }

        if (request == null || request.getDetalles() == null || request.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("El pedido debe tener al menos un producto");
        }

        User usuario = userService.findByUsername(username);
        return crearPedidoInterno(usuario, request);
    }

    private PedidoResponse crearPedidoInterno(User usuario, CrearPedidoRequest request) {
        Pedido pedido = new Pedido(usuario, request.getObservaciones());

        for (DetallePedidoRequest detReq : request.getDetalles()) {

            boolean tieneProducto = detReq.getProductId() != null;
            boolean tienePromo = detReq.getPromotionId() != null;

            if (tieneProducto) {
                Product producto = productService.findById(detReq.getProductId());
                pedido.agregarDetalle(new DetallePedido(producto, detReq.getCantidad()));
                continue;
            }

            
            var promo = promotionService.findPromotion(detReq.getPromotionId());
            promo.calculatePrices(); 

            BigDecimal original = promo.getOriginalPrice();
            BigDecimal finalP = promo.getFinalPrice();

            if (original == null || original.compareTo(BigDecimal.ZERO) == 0) {
                
                for (var item : promo.getItems()) {
                    Product prod = item.getProduct();
                    pedido.agregarDetalle(
                            new DetallePedido(prod, detReq.getCantidad(), BigDecimal.ZERO)
                    );
                }
                continue;
            }

            BigDecimal ratio = finalP.divide(original, 4, RoundingMode.HALF_UP);

            var items = promo.getItems().stream().toList();
            BigDecimal sumDi = BigDecimal.ZERO;

            for (int i = 0; i < items.size(); i++) {
                Product prod = items.get(i).getProduct();
                BigDecimal oi = prod.getPrice();

                BigDecimal di;
                if (i < items.size() - 1) {
                    di = oi.multiply(ratio).setScale(2, RoundingMode.HALF_UP);
                    sumDi = sumDi.add(di);
                } else {
                    di = finalP.subtract(sumDi).setScale(2, RoundingMode.HALF_UP);
                }

                pedido.agregarDetalle(
                        new DetallePedido(prod, detReq.getCantidad(), di)
                );
            }
        }

        aplicarDescuentoPorMontoMinimo(pedido);

        if (!pedido.tieneStockSuficiente()) {
            pedido.rechazar("sistema");
        }

        Pedido guardado = pedidoRepository.save(pedido);
        return convertirAResponse(guardado);
    }


    public void cancelarPedido(Long pedidoId, String username) {
        if (pedidoId == null) {
            throw new IllegalArgumentException("El ID del pedido es obligatorio");
        }

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El username del usuario es obligatorio");
        }

        Pedido pedido = pedidoRepository.findByIdWithDetalles(pedidoId)
            .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));

        User usuario = userService.findByUsername(username);

        if (!pedido.getUsuario().getUsername().equals(username)) {
            throw new IllegalArgumentException("No tienes permisos para cancelar este pedido");
        }

        if (!pedido.puedeCancelarse()) {
            throw new IllegalArgumentException("El pedido no se puede cancelar en su estado actual: " + 
                pedido.getEstado().getDescripcion());
        }

        pedido.cancelar(username);
        pedidoRepository.save(pedido);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> obtenerPedidosActivos() {
        List<Pedido> pedidos = pedidoRepository.findPedidosActivos();
        return pedidos.stream()
            .map(this::convertirAResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> obtenerHistorialPedidos() {
        List<Pedido> pedidos = pedidoRepository.findHistorial();
        return pedidos.stream()
            .map(this::convertirAResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> obtenerPedidosPorUsuario(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El username del usuario es obligatorio");
        }

        User usuario = userService.findByUsername(username);
        List<Pedido> pedidos = pedidoRepository.findByUsuarioOrderByFechaCreacionDesc(usuario);
        
        return pedidos.stream()
            .map(this::convertirAResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PedidoResponse obtenerPedidoPorId(Long pedidoId) {
        if (pedidoId == null) {
            throw new IllegalArgumentException("El ID del pedido es obligatorio");
        }

        Pedido pedido = pedidoRepository.findByIdWithDetalles(pedidoId)
            .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));

        return convertirAResponse(pedido);
    }

    public PedidoResponse cambiarEstadoPedido(Long pedidoId, EstadoPedido nuevoEstado, String emailUsuario) {
        if (pedidoId == null) {
            throw new IllegalArgumentException("El ID del pedido es obligatorio");
        }

        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El nuevo estado es obligatorio");
        }

        if (emailUsuario == null || emailUsuario.trim().isEmpty()) {
            throw new IllegalArgumentException("El email del usuario es obligatorio");
        }

        Pedido pedido = pedidoRepository.findByIdWithDetalles(pedidoId)
            .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));

        pedido.cambiarEstado(nuevoEstado, emailUsuario);

        if (nuevoEstado == EstadoPedido.EN_PREPARACION) {
            descontarStock(pedido);
        }

        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        return convertirAResponse(pedidoGuardado);
    }

    private void descontarStock(Pedido pedido) {
        for (DetallePedido detalle : pedido.getDetalles()) {
            Product producto = detalle.getProduct();
            
            
            Integer stockDisponible = producto.getCurrentStock();
            if (stockDisponible < detalle.getCantidad()) {
                throw new IllegalArgumentException(
                    String.format("Stock insuficiente para el producto %s. Disponible: %d, Solicitado: %d",
                        producto.getName(), stockDisponible, detalle.getCantidad())
                );
            }
            
            
            producto.decreaseStockWithIngredients(detalle.getCantidad());
            productService.save(producto);
        }
    }

    private PedidoResponse convertirAResponse(Pedido pedido) {
        List<DetallePedidoResponse> detallesResponse = pedido.getDetalles().stream()
            .map(detalle -> new DetallePedidoResponse(
                detalle.getId(),
                detalle.getProduct().getId(),
                detalle.getProduct().getName(),
                detalle.getCantidad(),
                detalle.getPrecioUnitario(),
                detalle.getSubtotal()
            ))
            .collect(Collectors.toList());

        return new PedidoResponse(
            pedido.getId(),
            pedido.getUsuario().getId(),
            pedido.getUsuario().getNombre() + " " + pedido.getUsuario().getApellido(),
            pedido.getEstado(),
            pedido.getFechaCreacion(),
            pedido.getFechaActualizacion(),
            pedido.getTotal(),
            pedido.getObservaciones(),
            pedido.getUsuarioModificacion(),
            detallesResponse,
            pedido.getAppliedPromotionId(),
            pedido.getAppliedPromotionName(),
            pedido.getDiscountAmount(),
            pedido.getSubtotalBeforeDiscount()
        );
    }

    private void aplicarDescuentoPorMontoMinimo(Pedido pedido) {
        var now = LocalDateTime.now();

        
        List<Promotion> promos = promotionService
                .findActiveNowEntities(now)
                .stream()
                .filter(p -> p.getType() == PromotionType.AMOUNT_DISCOUNT)
                .toList();

        if (promos.isEmpty()) return;

        BigDecimal subtotal = pedido.getTotal();

        
        Promotion best = promos.stream()
                .filter(p -> p.getMinimumPurchaseAmount() != null
                        && subtotal.compareTo(p.getMinimumPurchaseAmount()) >= 0)
                .max(Comparator.comparing(Promotion::getDiscountAmount))
                .orElse(null);

        if (best == null || best.getDiscountAmount() == null) return;

        
        pedido.setSubtotalBeforeDiscount(subtotal);

        BigDecimal newTotal = subtotal.subtract(best.getDiscountAmount());
        if (newTotal.compareTo(BigDecimal.ZERO) < 0) newTotal = BigDecimal.ZERO;

        pedido.setAppliedPromotionId(best.getId());
        pedido.setAppliedPromotionName(best.getName());
        pedido.setDiscountAmount(best.getDiscountAmount());
        pedido.setTotal(newTotal);
    }
}