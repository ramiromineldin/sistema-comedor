import { OrderCard } from "@/components/MyOrders/OrderCard/OrderCard";
import { PedidoResponse } from "@/types/Pedido";
import styles from './MyOrdersList.module.css';

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

interface MyOrdersListProps {
  pedidos: PedidoResponse[];
  promotions: Promotion[];
  onUpdate: () => void;
  loading?: boolean;
}

export function MyOrdersList({ pedidos, promotions, onUpdate, loading }: MyOrdersListProps) {
  if (loading) {
    return (
      <div className={styles.loading}>
        <div className={styles.spinner}></div>
        <p>Cargando pedidos...</p>
      </div>
    );
  }

  if (pedidos.length === 0) {
    return (
      <div className={styles.empty}>
        <h3>No tenés pedidos aún</h3>
        <p>Cuando hagas un pedido aparecerá acá.</p>
      </div>
    );
  }

  return (
    <div className={styles.container}>
      <div className={styles.title}>
        <h2>Mis Pedidos ({pedidos.length})</h2>
      </div>

      <div className={styles.list}>
        {pedidos.map((pedido) => (
          <OrderCard 
            key={pedido.id} 
            pedido={pedido} 
            promotions={promotions}
            onUpdate={onUpdate}
          />
        ))}
      </div>
    </div>
  );
}