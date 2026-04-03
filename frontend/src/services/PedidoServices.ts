import { buildApiUrl } from '../config/env-config';
import { CrearPedidoRequest, PedidoResponse, EstadoPedido } from '@/types/Pedido';

const getAuthHeaders = () => {
  const storedTokens = localStorage.getItem('tokens');
  let token = null;
  
  if (storedTokens) {
    try {
      const tokens = JSON.parse(storedTokens);
      token = tokens.accessToken;
    } catch (error) {
      console.error('Error parsing tokens from localStorage:', error);
    }
  }
  
  return {
    'Content-Type': 'application/json',
    ...(token && { 'Authorization': `Bearer ${token}` })
  };
};

export const PedidoServices = {
  // Para estudiantes
  async crearPedido(pedido: CrearPedidoRequest): Promise<PedidoResponse> {
    const response = await fetch(buildApiUrl('/pedidos'), {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(pedido)
    });
    
    if (!response.ok) {
      throw new Error(`Error al crear pedido: ${response.status}`);
    }
    
    return response.json();
  },

  async obtenerMisPedidos(): Promise<PedidoResponse[]> {
    const response = await fetch(buildApiUrl('/pedidos/mis-pedidos'), {
      method: 'GET',
      headers: getAuthHeaders()
    });
    
    if (!response.ok) {
      throw new Error(`Error al obtener pedidos: ${response.status}`);
    }
    
    return response.json();
  },

  async cancelarPedido(pedidoId: number): Promise<void> {
    const response = await fetch(buildApiUrl(`/pedidos/${pedidoId}/cancelar`), {
      method: 'PATCH',
      headers: getAuthHeaders()
    });
    
    if (!response.ok) {
      throw new Error(`Error al cancelar pedido: ${response.status}`);
    }
  },

  // Para personal de cocina
  async obtenerPedidosActivos(): Promise<PedidoResponse[]> {
    const response = await fetch(buildApiUrl('/pedidos/activos'), {
      method: 'GET',
      headers: getAuthHeaders()
    });
    
    if (!response.ok) {
      throw new Error(`Error al obtener pedidos activos: ${response.status}`);
    }
    
    return response.json();
  },

  async obtenerHistorialPedidos(): Promise<PedidoResponse[]> {
    const response = await fetch(buildApiUrl('/pedidos/historial'), {
      method: 'GET',
      headers: getAuthHeaders()
    });
    
    if (!response.ok) {
      throw new Error(`Error al obtener historial de pedidos: ${response.status}`);
    }
    
    return response.json();
  },

  async cambiarEstadoPedido(pedidoId: number, nuevoEstado: EstadoPedido): Promise<PedidoResponse> {
    const response = await fetch(buildApiUrl(`/pedidos/${pedidoId}/estado?nuevoEstado=${nuevoEstado}`), {
      method: 'PATCH',
      headers: getAuthHeaders()
    });
    
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`Error al cambiar estado del pedido: ${response.status} - ${errorText}`);
    }
    
    return response.json();
  }
};