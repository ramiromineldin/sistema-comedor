import React from 'react';
import { PedidoResponse, EstadoPedido } from '@/types/Pedido';
import styles from './PedidoCard.module.css';

interface PedidoCardProps {
  pedido: PedidoResponse;
  onCambiarEstado?: (pedidoId: number, nuevoEstado: EstadoPedido) => void;
  showActions?: boolean;
}

const PedidoCard: React.FC<PedidoCardProps> = ({ 
  pedido, 
  onCambiarEstado, 
  showActions = true 
}) => {
  const getEstadoClass = (estado: EstadoPedido) => {
    switch (estado) {
      case EstadoPedido.PENDIENTE:
        return styles.pendiente;
      case EstadoPedido.EN_PREPARACION:
        return styles.enPreparacion;
      case EstadoPedido.PREPARADO:
        return styles.preparado;
      case EstadoPedido.ENTREGADO:
        return styles.entregado;
      case EstadoPedido.CANCELADO:
        return styles.cancelado;
      case EstadoPedido.RECHAZADO:
        return styles.rechazado;
      default:
        return '';
    }
  };

  const getNextStates = (currentState: EstadoPedido): EstadoPedido[] => {
    switch (currentState) {
      case EstadoPedido.PENDIENTE:
        return [EstadoPedido.EN_PREPARACION, EstadoPedido.RECHAZADO];
      case EstadoPedido.EN_PREPARACION:
        return [EstadoPedido.PREPARADO];
      case EstadoPedido.PREPARADO:
        return [EstadoPedido.ENTREGADO];
      default:
        return [];
    }
  };

  const formatFecha = (fecha: string) => {
    return new Date(fecha).toLocaleString('es-AR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const getStateLabel = (estado: EstadoPedido) => {
    switch (estado) {
      case EstadoPedido.PENDIENTE:
        return 'Pendiente';
      case EstadoPedido.EN_PREPARACION:
        return 'En Preparación';
      case EstadoPedido.PREPARADO:
        return 'Preparado';
      case EstadoPedido.ENTREGADO:
        return 'Entregado';
      case EstadoPedido.CANCELADO:
        return 'Cancelado';
      case EstadoPedido.RECHAZADO:
        return 'Rechazado';
      default:
        return estado;
    }
  };

  const nextStates = getNextStates(pedido.estado);

  return (
    <div className={styles.pedidoCard}>
      <div className={styles.header}>
        <div className={styles.pedidoInfo}>
          <h3>Pedido #{pedido.id}</h3>
          <p className={styles.cliente}>{pedido.nombreUsuario}</p>
        </div>
        <div className={`${styles.estado} ${getEstadoClass(pedido.estado)}`}>
          {getStateLabel(pedido.estado)}
        </div>
      </div>

      <div className={styles.detalles}>
        <h4>Productos:</h4>
        {pedido.detalles.map((detalle) => (
          <div key={detalle.id} className={styles.producto}>
            <span className={styles.cantidad}>{detalle.cantidad}x</span>
            <span className={styles.nombre}>{detalle.productName}</span>
            <span className={styles.precio}>${detalle.subtotal.toFixed(2)}</span>
          </div>
        ))}
      </div>

      {pedido.observaciones && (
        <div className={styles.observaciones}>
          <strong>Observaciones:</strong> {pedido.observaciones}
        </div>
      )}

      <div className={styles.footer}>
        <div className={styles.tiempo}>
          <small>Creado: {formatFecha(pedido.fechaCreacion)}</small>
          {pedido.fechaActualizacion !== pedido.fechaCreacion && (
            <small>Actualizado: {formatFecha(pedido.fechaActualizacion)}</small>
          )}
        </div>
        <div className={styles.total}>
          <strong>Total: ${pedido.total.toFixed(2)}</strong>
        </div>
      </div>

      {showActions && nextStates.length > 0 && onCambiarEstado && (
        <div className={styles.actions}>
          {nextStates.map((estado) => (
            <button
              key={estado}
              className={`${styles.actionBtn} ${getEstadoClass(estado)}`}
              onClick={() => onCambiarEstado(pedido.id, estado)}
            >
              {estado === EstadoPedido.EN_PREPARACION && 'Comenzar Preparación'}
              {estado === EstadoPedido.PREPARADO && 'Marcar Preparado'}
              {estado === EstadoPedido.ENTREGADO && 'Marcar Entregado'}
              {estado === EstadoPedido.RECHAZADO && 'Rechazar'}
            </button>
          ))}
        </div>
      )}
    </div>
  );
};

export default PedidoCard;