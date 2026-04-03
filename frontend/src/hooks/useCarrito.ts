import { useState } from 'react';
import { PedidoServices } from '@/services/PedidoServices';
import { CrearPedidoRequest, DetallePedidoRequest } from '@/types/Pedido';

interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  currentStock: number;
}

interface Promotion {
  id: number;
  name: string;
  finalPrice: number;
}

type ItemType = 'PRODUCT' | 'PROMOTION';

interface ItemBase {
  key: string;              // P-1 o PR-10
  nombreProducto: string;
  precio: number;           // unitario final
  cantidad: number;
  type: ItemType;
}

interface ItemProducto extends ItemBase {
  type: 'PRODUCT';
  productId: number;
}

interface ItemPromocion extends ItemBase {
  type: 'PROMOTION';
  promotionId: number;
}

type ItemCarrito = ItemProducto | ItemPromocion;

export const useCarrito = () => {
  const [carrito, setCarrito] = useState<ItemCarrito[]>([]);
  const [mostrarCarrito, setMostrarCarrito] = useState(false);
  const [procesandoPedido, setProcesandoPedido] = useState(false);
  const [observaciones, setObservaciones] = useState('');

  // ====== PRODUCTOS ======
  const agregarAlCarrito = (product: Product, cantidad: number = 1) => {
    setCarrito(prev => {
      const key = `P-${product.id}`;
      const existente = prev.find(item => item.key === key);

      if (existente && existente.type === 'PRODUCT') {
        return prev.map(item =>
          item.key === key
            ? { ...item, cantidad: item.cantidad + cantidad }
            : item
        );
      }

      const nuevo: ItemProducto = {
        key,
        type: 'PRODUCT',
        productId: product.id,
        nombreProducto: product.name,
        precio: product.price,
        cantidad
      };

      return [...prev, nuevo];
    });

    setMostrarCarrito(true);
  };

  // ====== PROMOCIONES ======
  const agregarPromocion = (promo: Promotion, cantidad: number = 1) => {
    setCarrito(prev => {
      const key = `PR-${promo.id}`;
      const existente = prev.find(item => item.key === key);

      if (existente && existente.type === 'PROMOTION') {
        return prev.map(item =>
          item.key === key
            ? { ...item, cantidad: item.cantidad + cantidad }
            : item
        );
      }

      const nuevo: ItemPromocion = {
        key,
        type: 'PROMOTION',
        promotionId: promo.id,
        nombreProducto: promo.name,
        precio: promo.finalPrice,
        cantidad
      };

      return [...prev, nuevo];
    });

    setMostrarCarrito(true);
  };

  // Compat: acepta productId (number) o key (string)
  const removerDelCarrito = (idOrKey: number | string) => {
    const key = typeof idOrKey === 'number' ? `P-${idOrKey}` : idOrKey;
    setCarrito(prev => prev.filter(item => item.key !== key));
  };

  // Compat: acepta productId (number) o key (string)
  const actualizarCantidad = (idOrKey: number | string, nuevaCantidad: number) => {
    const key = typeof idOrKey === 'number' ? `P-${idOrKey}` : idOrKey;

    if (nuevaCantidad <= 0) {
      removerDelCarrito(key);
      return;
    }

    setCarrito(prev =>
      prev.map(item =>
        item.key === key
          ? { ...item, cantidad: nuevaCantidad }
          : item
      )
    );
  };

  const vaciarCarrito = () => {
    setCarrito([]);
    setObservaciones('');
    setMostrarCarrito(false);
  };

  const calcularTotal = () => {
    return carrito.reduce((total, item) => total + (item.precio * item.cantidad), 0);
  };

  const calcularCantidadItems = () => {
    return carrito.reduce((total, item) => total + item.cantidad, 0);
  };

  const realizarPedido = async () => {
    if (carrito.length === 0) {
      throw new Error('El carrito está vacío');
    }

    setProcesandoPedido(true);
    try {
      const detalles: DetallePedidoRequest[] = carrito.map(item => {
        if (item.type === 'PRODUCT') {
          return { productId: item.productId, cantidad: item.cantidad };
        }
        return { promotionId: item.promotionId, cantidad: item.cantidad };
      });

      const pedido: CrearPedidoRequest = {
        detalles,
        observaciones: observaciones.trim() || undefined
      };

      await PedidoServices.crearPedido(pedido);

      vaciarCarrito();

      return {
        success: true,
        message: '¡Pedido realizado exitosamente! Puedes verlo en "Mis Pedidos".'
      };
    } catch (error) {
      console.error('Error al realizar pedido:', error);
      throw new Error('Error al realizar el pedido. Por favor, inténtalo nuevamente.');
    } finally {
      setProcesandoPedido(false);
    }
  };

  // API vieja para pantallas de productos
  const handleOrderItem = (product: Product, cantidad: number = 1) => {
    agregarAlCarrito(product, cantidad);
  };

  const toggleMostrarCarrito = () => {
    setMostrarCarrito(!mostrarCarrito);
  };

  return {
    carrito,
    mostrarCarrito,
    procesandoPedido,
    observaciones,

    agregarAlCarrito,
    agregarPromocion,

    removerDelCarrito,
    actualizarCantidad,

    vaciarCarrito,
    realizarPedido,

    handleOrderItem,
    toggleMostrarCarrito,
    setObservaciones,

    calcularTotal,
    calcularCantidadItems,

    tieneItems: carrito.length > 0
  };
};