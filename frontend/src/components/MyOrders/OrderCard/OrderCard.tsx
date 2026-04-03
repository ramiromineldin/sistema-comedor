import { PedidoResponse, EstadoPedido, DetallePedidoResponse } from '@/types/Pedido';
import { PedidoServices } from '@/services/PedidoServices';
import styles from './OrderCard.module.css';

interface PromotionItem {
  productId: number;
  productName: string;
  originalPrice: number;
  inStock: boolean;
}

interface Promotion {
  id: number;
  name: string;
  type: string;
  finalPrice?: number;
  discountPercentage?: number;
  discountAmount?: number;
  minimumPurchaseAmount?: number;
  items: PromotionItem[];
}

interface PromoGroup {
  promotion: Promotion;
  cantidad: number;      // cuántas promos se compraron
  totalPromo: number;    // finalPrice * cantidad
}

interface OrderCardProps {
  pedido: PedidoResponse;
  promotions: Promotion[];
  onUpdate: () => void;
}

function groupDetallesWithPromos(
  detalles: DetallePedidoResponse[],
  promotions: Promotion[]
): { promoGroups: PromoGroup[]; remaining: DetallePedidoResponse[] } {
  // Primero separar los que ya tienen promotionId (nuevos) de los demás
  const detallesSinPromo = detalles.filter(d => !d.promotionId);

  const remaining = detallesSinPromo.map(d => ({ ...d }));
  const promoGroups: PromoGroup[] = [];
  const EPS = 0.05;

  // Solo intentar agrupar heurísticamente los detalles sin promotionId
  for (const promo of promotions) {
    if (!promo.finalPrice || isNaN(promo.finalPrice)) continue;
    
    const ids = promo.items.map(i => i.productId);

    const chosenLines = ids.map(id => {
      const lines = remaining.filter(d => d.productId === id);
      if (lines.length === 0) return null;
      return [...lines].sort((a, b) => a.precioUnitario - b.precioUnitario)[0];
    });

    if (chosenLines.some(l => l == null)) continue;
    const lines = chosenLines as DetallePedidoResponse[];

    const q = Math.min(...lines.map(l => l.cantidad));
    if (q <= 0) continue;

    const totalTaken = lines.reduce((acc, l) => acc + l.precioUnitario * q, 0);
    const expected = promo.finalPrice * q;

    if (Math.abs(totalTaken - expected) > EPS) continue;

    for (const l of lines) {
      l.cantidad -= q;
      l.subtotal = l.precioUnitario * l.cantidad;
    }

    for (let i = remaining.length - 1; i >= 0; i--) {
      if (remaining[i].cantidad <= 0) remaining.splice(i, 1);
    }

    promoGroups.push({
      promotion: promo,
      cantidad: q,
      totalPromo: expected
    });
  }

  return { promoGroups, remaining };
}

export function OrderCard({ pedido, promotions, onUpdate }: OrderCardProps) {
  const handleCancelar = async () => {
    if (!confirm('¿Estás seguro de que quieres cancelar este pedido?')) return;
    try {
      await PedidoServices.cancelarPedido(pedido.id);
      onUpdate();
    } catch (error) {
      console.error(error);
      alert('No se pudo cancelar el pedido.');
    }
  };

  const getEstadoColor = (estado: EstadoPedido) => {
    switch (estado) {
      case EstadoPedido.PENDIENTE: return '#f39c12';
      case EstadoPedido.EN_PREPARACION: return '#3498db';
      case EstadoPedido.PREPARADO: return '#27ae60';
      case EstadoPedido.ENTREGADO: return '#2ecc71';
      case EstadoPedido.CANCELADO:
      case EstadoPedido.RECHAZADO: return '#e74c3c';
      default: return '#7f8c8d';
    }
  };

  const { promoGroups, remaining } = groupDetallesWithPromos(pedido.detalles, promotions);

  // Detectar si hay descuento por monto mínimo aplicado
  const detectarDescuentoPorMonto = () => {
    const tienePromoCombo = pedido.detalles.some(d => d.promotionId);
    if (tienePromoCombo) return null;

    const subtotal = pedido.detalles.reduce((sum, d) => sum + d.subtotal, 0);
    const descuentoAplicado = subtotal - pedido.total;

    if (descuentoAplicado > 0.01) {
      // Buscar qué promoción por monto mínimo pudo haberse aplicado
      const promoPosible = promotions.find(p => 
        (p.type === 'AMOUNT_DISCOUNT' || p.type === 'PERCENTAGE_DISCOUNT') &&
        p.minimumPurchaseAmount && 
        subtotal >= p.minimumPurchaseAmount
      );

      return {
        subtotal,
        descuento: descuentoAplicado,
        promoName: promoPosible?.name || 'Descuento por monto'
      };
    }

    return null;
  };

  const descuentoInfo = detectarDescuentoPorMonto();

  return (
    <div className={styles.card}>
      <div className={styles.header}>
        {/* ✅ restaurado para que el h3 tenga color oscuro */}
        <div className={styles.pedidoInfo}>
          <h3>Pedido #{pedido.id}</h3>
          <p className={styles.fecha}>
            {new Date(pedido.fechaCreacion).toLocaleString()}
          </p>
        </div>

        <div
          className={styles.estado}
          style={{ backgroundColor: getEstadoColor(pedido.estado) }}
        >
          {pedido.estado.replace('_', ' ')}
        </div>
      </div>

      <div className={styles.body}>
        <div className={styles.productos}>
          <h4>Productos:</h4>
          <ul>
            {/* ===== DETALLES CON PROMOTION ID (del backend) ===== */}
            {pedido.detalles
              .filter(d => d.promotionId)
              .map(detalle => {
                const promo = promotions.find(p => p.id === detalle.promotionId);
                return (
                  <li key={detalle.id} className={styles.producto}>
                    <div
                      style={{
                        backgroundColor: "#e8f5e9",
                        padding: "8px",
                        borderRadius: "4px",
                        marginBottom: "8px"
                      }}
                    >
                      <div style={{
                        display: "flex",
                        justifyContent: "space-between",
                        fontWeight: 700,
                        color: "#2c3e50",
                        marginBottom: "4px"
                      }}>
                        <span>{detalle.cantidad}x {detalle.promotionName || promo?.name || 'Promoción'}</span>
                        <span>${detalle.subtotal.toFixed(2)}</span>
                      </div>
                      {promo && (
                        <ul style={{ marginTop: 6, marginLeft: 16, fontSize: "0.9em", color: "#555" }}>
                          {promo.items.map(it => (
                            <li key={it.productId}>• {it.productName}</li>
                          ))}
                        </ul>
                      )}
                    </div>
                  </li>
                );
              })
            }

            {/* ===== PROMOS AGRUPADAS (heurística para pedidos viejos) ===== */}
            {promoGroups.map(pg => (
              <li key={`promo-${pg.promotion.id}`} className={styles.producto}>
                <div
                  style={{
                    display: "flex",
                    justifyContent: "space-between",
                    fontWeight: 700,
                    color: "#2c3e50",
                    backgroundColor: "#e8f5e9",
                    padding: "8px",
                    borderRadius: "4px",
                    marginBottom: "8px"
                  }}
                >
                  <span>{pg.promotion.name}</span>
                  <span>${pg.totalPromo.toFixed(2)}</span>
                </div>

                <ul style={{ marginTop: 6, marginLeft: 16, marginBottom: 12 }}>
                  {pg.promotion.items.map(it => (
                    <li
                      key={it.productId}
                      style={{
                        display: "flex",
                        justifyContent: "space-between",
                        color: "#555",
                        fontSize: "0.9em"
                      }}
                    >
                      <span>  • {pg.cantidad}x {it.productName}</span>
                    </li>
                  ))}
                </ul>
              </li>
            ))}

            {/* ===== PRODUCTOS SUELTOS ===== */}
            {remaining.map(detalle => (
              <li key={detalle.id} className={styles.producto}>
                <span className={styles.cantidad}>{detalle.cantidad}x</span>
                <span className={styles.nombre}>{detalle.productName}</span>
                <span className={styles.precio}>${detalle.subtotal.toFixed(2)}</span>
              </li>
            ))}
          </ul>
        </div>

        {pedido.observaciones && (
          <div className={styles.observaciones}>
            <strong>Observaciones:</strong> {pedido.observaciones}
          </div>
        )}

        {descuentoInfo && (
          <div style={{
            backgroundColor: '#e8f5e9',
            padding: '10px',
            borderRadius: '6px',
            marginTop: '15px',
            marginBottom: '10px'
          }}>
            <div style={{ 
              fontSize: '0.9em', 
              color: '#2e7d32',
              marginBottom: '6px',
              fontWeight: 'bold'
            }}>
              {descuentoInfo.promoName}
            </div>
            <div style={{ 
              display: 'flex', 
              justifyContent: 'space-between',
              fontSize: '0.85em',
              color: '#555',
              marginBottom: '2px'
            }}>
              <span>Subtotal:</span>
              <span>${descuentoInfo.subtotal.toFixed(2)}</span>
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
        )}

        <div className={styles.total}>
          <strong>Total: ${pedido.total.toFixed(2)}</strong>
        </div>
      </div>

      <div className={styles.actions}>
        {pedido.estado === EstadoPedido.PENDIENTE && (
          <button
            className={styles.cancelarBtn}
            onClick={handleCancelar}
          >
            Cancelar Pedido
          </button>
        )}

        {pedido.estado === EstadoPedido.PREPARADO && (
          <div className={styles.listoMsg}>
            ¡Listo para retirar!
          </div>
        )}
      </div>
    </div>
  );
}