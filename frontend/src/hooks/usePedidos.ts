import { useState, useEffect, useCallback } from 'react';
import { PedidoServices } from '@/services/PedidoServices';
import { PedidoResponse, EstadoPedido } from '@/types/Pedido';

export const usePedidos = () => {
  const [pedidos, setPedidos] = useState<PedidoResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const cargarPedidos = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const misPedidos = await PedidoServices.obtenerMisPedidos();
      setPedidos(misPedidos.sort((a, b) => 
        new Date(b.fechaCreacion).getTime() - new Date(a.fechaCreacion).getTime()
      ));
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al cargar pedidos');
    } finally {
      setLoading(false);
    }
  }, []);

  const cancelarPedido = useCallback(async (pedidoId: number) => {
    try {
      await PedidoServices.cancelarPedido(pedidoId);
      await cargarPedidos();
      return true;
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al cancelar pedido');
      return false;
    }
  }, [cargarPedidos]);

  useEffect(() => {
    cargarPedidos();
  }, [cargarPedidos]);

  return {
    pedidos,
    loading,
    error,
    cargarPedidos,
    cancelarPedido,
    clearError: () => setError(null)
  };
};

export const usePedidosActivos = () => {
  const [pedidos, setPedidos] = useState<PedidoResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const cargarPedidosActivos = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const pedidosActivos = await PedidoServices.obtenerPedidosActivos();
      setPedidos(pedidosActivos.sort((a, b) => 
        new Date(a.fechaCreacion).getTime() - new Date(b.fechaCreacion).getTime()
      ));
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al cargar pedidos activos');
    } finally {
      setLoading(false);
    }
  }, []);

  const cambiarEstadoPedido = useCallback(async (pedidoId: number, nuevoEstado: EstadoPedido) => {
    try {
      await PedidoServices.cambiarEstadoPedido(pedidoId, nuevoEstado);
      await cargarPedidosActivos();
      return true;
    } catch (err) {
      console.error('Error al cambiar estado:', err);
      setError(err instanceof Error ? err.message : 'Error al cambiar estado del pedido');
      return false;
    }
  }, [cargarPedidosActivos]);

  useEffect(() => {
    cargarPedidosActivos();
  }, [cargarPedidosActivos]);

  return {
    pedidos,
    loading,
    error,
    cargarPedidosActivos,
    cambiarEstadoPedido,
    clearError: () => setError(null)
  };
};

export const useHistorialPedidos = () => {
  const [pedidos, setPedidos] = useState<PedidoResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const cargarHistorial = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const historial = await PedidoServices.obtenerHistorialPedidos();
      setPedidos(historial.sort((a, b) => 
        new Date(b.fechaCreacion).getTime() - new Date(a.fechaCreacion).getTime()
      ));
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al cargar historial');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    cargarHistorial();
  }, [cargarHistorial]);

  return {
    pedidos,
    loading,
    error,
    cargarHistorial,
    clearError: () => setError(null)
  };
};