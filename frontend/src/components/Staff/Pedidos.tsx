
import { usePedidosActivos } from '@/hooks/usePedidos';
import { EstadoPedido } from '@/types/Pedido';
import PedidoCard from '@/components/Cocina/PedidoCard/PedidoCard';
import styles from './Pedidos.module.css';

const Pedidos: React.FC = () => {
  
  const {
    pedidos: pedidosActivos,
    loading: loadingActivos,
    error: errorActivos,
    cambiarEstadoPedido,
    clearError: clearErrorActivos
  } = usePedidosActivos();

  

  const handleCambiarEstado = async (pedidoId: number, nuevoEstado: EstadoPedido) => {
    const success = await cambiarEstadoPedido(pedidoId, nuevoEstado);
    if (!success){
        alert("No se pudo actualizar el estado")
    }

  };

  const filtrarPedidosPorEstado = (estado: EstadoPedido) => {
    return pedidosActivos.filter(pedido => pedido.estado === estado);
  };

  const pedidosPendientes = filtrarPedidosPorEstado(EstadoPedido.PENDIENTE);
  const pedidosEnPreparacion = filtrarPedidosPorEstado(EstadoPedido.EN_PREPARACION);
  const pedidosPreparados = filtrarPedidosPorEstado(EstadoPedido.PREPARADO);

  const currentLoading = loadingActivos;
  const currentError =errorActivos ;
  const currentClearError = clearErrorActivos ;

  if (currentError) {
    return (
      <div className={styles.error}>
        <h3>Error al cargar pedidos</h3>
        <p>{currentError}</p>
        <button onClick={currentClearError} className={styles.retryBtn}>
          Reintentar
        </button>
      </div>
    );
  }

  return (
      <div className={styles.gestionPedidos}>
        
        {currentLoading ? (
            <div className={styles.loading}>
            <div className={styles.spinner}></div>
            <p>Cargando pedidos...</p>
            </div>
        ) : (
            
                <div className={styles.activosContainer}>
                <div className={styles.columnas}>
                    <div className={styles.columna}>
                    <h2 className={styles.columnaTitle}>
                        Pendientes 
                        <span className={styles.badge}>{pedidosPendientes.length}</span>
                    </h2>
                    <div className={styles.pedidosList}>
                        {pedidosPendientes.length === 0 ? (
                        <div className={styles.empty}>No hay pedidos pendientes</div>
                        ) : (
                        pedidosPendientes.map(pedido => (
                            <PedidoCard
                            key={pedido.id}
                            pedido={pedido}
                            onCambiarEstado={handleCambiarEstado}
                            />
                        ))
                        )}
                    </div>
                    </div>

                    <div className={styles.columna}>
                    <h2 className={styles.columnaTitle}>
                        En Preparación
                        <span className={styles.badge}>{pedidosEnPreparacion.length}</span>
                    </h2>
                    <div className={styles.pedidosList}>
                        {pedidosEnPreparacion.length === 0 ? (
                        <div className={styles.empty}>No hay pedidos en preparación</div>
                        ) : (
                        pedidosEnPreparacion.map(pedido => (
                            <PedidoCard
                            key={pedido.id}
                            pedido={pedido}
                            onCambiarEstado={handleCambiarEstado}
                            />
                        ))
                        )}
                    </div>
                    </div>

                    <div className={styles.columna}>
                    <h2 className={styles.columnaTitle}>
                        Preparados
                        <span className={styles.badge}>{pedidosPreparados.length}</span>
                    </h2>
                    <div className={styles.pedidosList}>
                        {pedidosPreparados.length === 0 ? (
                        <div className={styles.empty}>No hay pedidos preparados</div>
                        ) : (
                        pedidosPreparados.map(pedido => (
                            <PedidoCard
                            key={pedido.id}
                            pedido={pedido}
                            onCambiarEstado={handleCambiarEstado}
                            />
                        ))
                        )}
                    </div>
                    </div>
                </div>
                </div>
        
        )}
      </div>
    
  );
};

export default Pedidos;