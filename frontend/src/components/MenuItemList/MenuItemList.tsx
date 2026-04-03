import { BiSolidDish } from 'react-icons/bi';
import { MenuItemCard } from "@/components/MenuItemCard";
import styles from "./MenuItemList.module.css";

interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  currentStock: number;
}

interface MenuItemListProps {
  products: Product[];
  onOrderItem?: (product: Product) => void;
}

export const MenuItemList = ({ products, onOrderItem }: MenuItemListProps) => {
  if (!products || products.length === 0) {
    return (
      <div className={styles.emptyState}>
        <div className={styles.emptyIcon}><BiSolidDish /></div>
        <h3>No hay productos disponibles</h3>
        <p>El menú de hoy aún no está disponible o no tiene productos asignados.</p>
      </div>
    );
  }

  return (
    <div className={styles.menuGrid}>
      {products.map((product) => (
        <MenuItemCard 
          key={product.id} 
          product={product} 
          onOrder={onOrderItem}
        />
      ))}
    </div>
  );
};