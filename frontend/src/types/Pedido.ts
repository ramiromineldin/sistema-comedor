export enum EstadoPedido {
  PENDIENTE = 'PENDIENTE',
  EN_PREPARACION = 'EN_PREPARACION', 
  PREPARADO = 'PREPARADO',
  ENTREGADO = 'ENTREGADO',
  CANCELADO = 'CANCELADO',
  RECHAZADO = 'RECHAZADO'
}

export interface DetallePedidoResponse {
  id: number;
  productId?: number;
  promotionId?: number;
  productName?: string;
  promotionName?: string;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
}

export interface PedidoResponse {
  id: number;
  userId: number;
  nombreUsuario: string;
  estado: EstadoPedido;
  fechaCreacion: string;
  fechaActualizacion: string;
  total: number;
  observaciones?: string;
  usuarioModificacion?: string;
  detalles: DetallePedidoResponse[];
  promotions?: Array<{
    id: number;
    name: string;
    finalPrice: number;
  }>;
}

export interface DetallePedidoRequest {
  productId?: number;
  promotionId?: number;
  cantidad: number;
}

export interface CrearPedidoRequest {
  detalles: DetallePedidoRequest[];
  observaciones?: string;
}