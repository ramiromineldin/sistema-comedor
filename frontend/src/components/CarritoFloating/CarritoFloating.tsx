import { useCarrito } from '@/hooks/useCarrito';
import { useQuery } from '@tanstack/react-query';
import { useAccessTokenGetter } from '@/services/TokenContext';
import { buildApiUrl } from '@/config/env-config';
import styles from './CarritoFloating.module.css';
import { MdLightbulb } from 'react-icons/md';

interface Promotion {
  id: number;
  name: string;
  type: string;
  active: boolean;
  available?: boolean;
  discountPercentage?: number;
  discountAmount?: number;
  minimumPurchaseAmount?: number;
}

interface CarritoFloatingProps {
  carrito: ReturnType<typeof useCarrito>;
}

export const CarritoFloating = ({ carrito }: CarritoFloatingProps) => {
  const getAccessToken = useAccessTokenGetter();
  
  const {
    carrito: items,
    mostrarCarrito,
    procesandoPedido,
    observaciones,
    calcularTotal,
    calcularCantidadItems,
    actualizarCantidad,
    removerDelCarrito,
    vaciarCarrito,
    realizarPedido,
    toggleMostrarCarrito,
    setObservaciones,
    tieneItems
  } = carrito;

  const { data: promotions = [] } = useQuery({
    queryKey: ["promotions"],
    queryFn: async (): Promise<Promotion[]> => {
      const token = await getAccessToken();
      const response = await fetch(buildApiUrl("/promotions"), {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });
      if (!response.ok) throw new Error('Error al cargar promociones');
      return response.json();
    },
  });

  // Calcular descuento aplicable por monto mínimo
  const calcularDescuentoPorMonto = () => {
    const total = calcularTotal();
    const tienePromocionCombo = items.some(item => item.type === 'PROMOTION');
    
    // Si ya tiene una promoción de combo, no aplicar descuento por monto
    if (tienePromocionCombo) return null;

    // Buscar promociones de monto mínimo aplicables
    const promosAplicables = promotions
      .filter(p => p.active && 
        (p.type === 'AMOUNT_DISCOUNT' || p.type === 'PERCENTAGE_DISCOUNT') &&
        p.minimumPurchaseAmount && 
        total >= p.minimumPurchaseAmount)
      .sort((a, b) => {
        // Ordenar por mejor descuento
        const descA = a.type === 'AMOUNT_DISCOUNT' ? (a.discountAmount || 0) : (total * (a.discountPercentage || 0) / 100);
        const descB = b.type === 'AMOUNT_DISCOUNT' ? (b.discountAmount || 0) : (total * (b.discountPercentage || 0) / 100);
        return descB - descA;
      });

    if (promosAplicables.length === 0) return null;

    const promo = promosAplicables[0];
    let descuento = 0;

    if (promo.type === 'AMOUNT_DISCOUNT') {
      descuento = promo.discountAmount || 0;
    } else if (promo.type === 'PERCENTAGE_DISCOUNT') {
      descuento = total * (promo.discountPercentage || 0) / 100;
    }

    return {
      promo,
      descuento,
      totalConDescuento: total - descuento
    };
  };

  const descuentoInfo = calcularDescuentoPorMonto();

  // Calcular cuánto falta para alcanzar descuentos
  const calcularFaltanteParaDescuento = () => {
    const total = calcularTotal();
    const tienePromocionCombo = items.some(item => item.type === 'PROMOTION');
    
    // Si ya tiene una promoción de combo, no mostrar mensaje
    if (tienePromocionCombo) return null;
    
    // Si ya tiene descuento aplicado, no mostrar
    if (descuentoInfo) return null;

    // Buscar promociones de monto mínimo activas
    const promosDisponibles = promotions
      .filter(p => p.active && 
        (p.type === 'AMOUNT_DISCOUNT' || p.type === 'PERCENTAGE_DISCOUNT') &&
        p.minimumPurchaseAmount && 
        total < p.minimumPurchaseAmount)
      .sort((a, b) => (a.minimumPurchaseAmount || 0) - (b.minimumPurchaseAmount || 0));

    if (promosDisponibles.length === 0) return null;

    const promo = promosDisponibles[0]; // La más cercana
    const faltante = (promo.minimumPurchaseAmount || 0) - total;

    return {
      promo,
      faltante,
      minimoPurchaseAmount: promo.minimumPurchaseAmount || 0
    };
  };

  const faltanteInfo = calcularFaltanteParaDescuento();

  const handleRealizarPedido = async () => {
    try {
      const resultado = await realizarPedido();
      alert(resultado.message);
    } catch (error: any) {
      alert(error.message);
    }
  };

  if (!tieneItems) return null;

  return (
    <div className={styles.carritoFloating}>
      <button 
        className={styles.carritoToggle}
        onClick={toggleMostrarCarrito}
      >
        🛒 Carrito ({calcularCantidadItems()}) - ${calcularTotal().toFixed(2)}
      </button>
      
      {mostrarCarrito && (
        <div className={styles.carritoModal}>
          <div className={styles.carritoContent}>
            <div className={styles.carritoHeader}>
              <h3>Tu Pedido</h3>
              <button 
                className={styles.carritoClose}
                onClick={toggleMostrarCarrito}
              >
                ×
              </button>
            </div>
            
            <div className={styles.carritoItems}>
              {items.map(item => (
                <div key={item.key} className={styles.carritoItem}>
                  <div className={styles.itemInfo}>
                    <h4>
                      {item.type === 'PROMOTION'}
                      {item.nombreProducto}
                      {item.type === 'PROMOTION' && ' (Promoción)'}
                    </h4>
                    <p>${item.precio.toFixed(2)}{item.type === 'PROMOTION' ? ' total' : ' c/u'}</p>
                  </div>

                  <div className={styles.itemControls}>
                    <button
                      onClick={() => actualizarCantidad(item.key, item.cantidad - 1)}
                      className={styles.cantidadBtn}
                    >
                      -
                    </button>
                    <span className={styles.cantidad}>{item.cantidad}</span>
                    <button
                      onClick={() => actualizarCantidad(item.key, item.cantidad + 1)}
                      className={styles.cantidadBtn}
                    >
                      +
                    </button>
                  </div>

                  <div className={styles.itemTotal}>
                    ${(item.precio * item.cantidad).toFixed(2)}
                  </div>

                  <button
                    onClick={() => removerDelCarrito(item.key)}
                    className={styles.removeBtn}
                  >
                    ×
                  </button>
                </div>
              ))}
            </div>
            
            {faltanteInfo && (
              <div style={{
                backgroundColor: '#fff3cd',
                border: '1px solid #ffc107',
                borderRadius: '6px',
                padding: '12px',
                margin: '10px 0',
                fontSize: '0.9em'
              }}>
                <div style={{ 
                  color: '#856404',
                  fontWeight: 'bold',
                  marginBottom: '4px',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '6px'
                }}>
                  <MdLightbulb size={18} />
                  <span>¡Estás cerca de un descuento!</span>
                </div>
                <div style={{ color: '#856404' }}>
                  Agregá <strong>${faltanteInfo.faltante.toFixed(2)}</strong> más para obtener:{' '}
                  <strong>{faltanteInfo.promo.name}</strong>
                </div>
                {faltanteInfo.promo.type === 'AMOUNT_DISCOUNT' && (
                  <div style={{ color: '#856404', fontSize: '0.85em', marginTop: '4px' }}>
                    (Descuento de ${faltanteInfo.promo.discountAmount?.toFixed(2)})
                  </div>
                )}
                {faltanteInfo.promo.type === 'PERCENTAGE_DISCOUNT' && (
                  <div style={{ color: '#856404', fontSize: '0.85em', marginTop: '4px' }}>
                    (Descuento del {faltanteInfo.promo.discountPercentage}%)
                  </div>
                )}
              </div>
            )}
            
            <div className={styles.observaciones}>
              <label htmlFor="obs">Observaciones (opcional):</label>
              <textarea
                id="obs"
                value={observaciones}
                onChange={(e) => setObservaciones(e.target.value)}
                placeholder="Ej: Sin cebolla, extra salsa..."
                maxLength={200}
                className={styles.observacionesInput}
              />
            </div>
            
            <div className={styles.carritoFooter}>
              <div className={styles.total}>
                {descuentoInfo ? (
                  <>
                    <div style={{ 
                      backgroundColor: '#e8f5e9', 
                      padding: '10px', 
                      borderRadius: '6px',
                      marginBottom: '10px'
                    }}>
                      <div style={{ 
                        fontSize: '0.9em', 
                        color: '#2e7d32',
                        marginBottom: '4px'
                      }}>
                        Promoción aplicada: {descuentoInfo.promo.name}
                      </div>
                      <div style={{ 
                        display: 'flex', 
                        justifyContent: 'space-between',
                        fontSize: '0.85em',
                        color: '#555'
                      }}>
                        <span>Subtotal:</span>
                        <span>${calcularTotal().toFixed(2)}</span>
                      </div>
                      <div style={{ 
                        display: 'flex', 
                        justifyContent: 'space-between',
                        fontSize: '0.85em',
                        color: '#2e7d32',
                        fontWeight: 'bold'
                      }}>
                        <span>Descuento:</span>
                        <span>-${descuentoInfo.descuento.toFixed(2)}</span>
                      </div>
                    </div>
                    <strong style={{ fontSize: '1.2em' }}>
                      Total: ${descuentoInfo.totalConDescuento.toFixed(2)}
                    </strong>
                  </>
                ) : (
                  <strong>Total: ${calcularTotal().toFixed(2)}</strong>
                )}
              </div>
              <div className={styles.carritoAcciones}>
                <button 
                  className={styles.vaciarBtn}
                  onClick={vaciarCarrito}
                >
                  Vaciar
                </button>
                <button 
                  className={styles.pedirBtn}
                  onClick={handleRealizarPedido}
                  disabled={procesandoPedido}
                >
                  {procesandoPedido ? 'Procesando...' : 'Realizar Pedido'}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};