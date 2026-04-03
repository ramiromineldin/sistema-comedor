package ar.uba.fi.ingsoft1.product_example.orders;

import ar.uba.fi.ingsoft1.product_example.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    List<Pedido> findByUsuarioOrderByFechaCreacionDesc(User usuario);
    
    List<Pedido> findByEstadoOrderByFechaCreacionAsc(EstadoPedido estado);
    
    @Query("SELECT p FROM Pedido p WHERE p.estado IN :estados ORDER BY p.fechaCreacion ASC")
    List<Pedido> findByEstadoInOrderByFechaCreacionAsc(@Param("estados") List<EstadoPedido> estados);
    
    List<Pedido> findByUsuarioAndEstadoOrderByFechaCreacionDesc(User usuario, EstadoPedido estado);
    
    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.detalles d LEFT JOIN FETCH d.product WHERE p.id = :id")
    Optional<Pedido> findByIdWithDetalles(@Param("id") Long id);
    
    long countByEstado(EstadoPedido estado);
    
    @Query("SELECT p FROM Pedido p WHERE p.estado IN ('ENTREGADO', 'CANCELADO', 'RECHAZADO') ORDER BY p.fechaActualizacion DESC")
    List<Pedido> findHistorial();
    
    @Query("SELECT p FROM Pedido p WHERE p.estado IN ('PENDIENTE', 'EN_PREPARACION', 'PREPARADO') ORDER BY p.fechaCreacion ASC")
    List<Pedido> findPedidosActivos();
}