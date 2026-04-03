import { MyOrdersList } from './MyOrdersList/MyOrdersList';
import { usePedidos } from '@/hooks/usePedidos';
import styles from './MyOrders.module.css';

import { useQuery } from "@tanstack/react-query";
import { useAccessTokenGetter } from "@/services/TokenContext";
import { buildApiUrl } from "@/config/env-config";

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

export function MyOrders() {
  const { 
    pedidos, 
    loading, 
    error, 
    cargarPedidos, 
    clearError 
  } = usePedidos();

  const getAccessToken = useAccessTokenGetter();

  const { data: promotionsData = [] } = useQuery({
    queryKey: ["promotions"],
    queryFn: async (): Promise<Promotion[]> => {
      const token = await getAccessToken();
      const response = await fetch(buildApiUrl("/promotions"), {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });
      if (!response.ok) {
        throw new Error(`Error ${response.status}: ${response.statusText}`);
      }
      return response.json();
    },
  });

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <h1>Mis Pedidos</h1>
        <button onClick={cargarPedidos} className={styles.refreshBtn}>
          Actualizar
        </button>
      </div>

      {error && (
        <div className={styles.error}>
          <strong>Error:</strong> {error}
          <button onClick={clearError}>x</button>
        </div>
      )}

      <MyOrdersList 
        pedidos={pedidos}
        promotions={promotionsData}
        onUpdate={cargarPedidos}
        loading={loading}
      />
    </div>
  );
}