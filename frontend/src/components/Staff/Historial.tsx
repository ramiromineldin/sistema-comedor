
import { useHistorialPedidos } from '@/hooks/usePedidos';
import PedidoCard from '@/components/Cocina/PedidoCard/PedidoCard';
import styles from './Pedidos.module.css';


export const Historial: React.FC = () => {
    

  const {
    pedidos: historial,
    loading: loadingHistorial,
    error: errorHistorial,
    clearError: clearErrorHistorial
  } = useHistorialPedidos();


 
  const currentLoading = loadingHistorial;
  const currentError =  errorHistorial;
  const currentClearError =  clearErrorHistorial;

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
            <div className={styles.historialContainer}>
              <div className={styles.pedidosList}>
                {historial.length === 0 ? (
                  <div className={styles.empty}>No hay pedidos en el historial</div>
                ) : (
                  historial.map(pedido => (
                    <PedidoCard
                      key={pedido.id}
                      pedido={pedido}
                      showActions={false}
                    />
                  ))
                )}
              </div>
            </div>
          )}
        
      </div>
    
  );

};
