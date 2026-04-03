import { useState } from 'react';
import styles from "./MenuItemCard.module.css";

interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  currentStock: number;
}

interface MenuItemCardProps {
  product: Product;
  onOrder?: (product: Product, cantidad: number) => void;
}

export const MenuItemCard = ({ product, onOrder }: MenuItemCardProps) => {
  const [cantidad, setCantidad] = useState(1);
  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('es-AR', {
      style: 'currency',
      currency: 'ARS'
    }).format(price);
  };

  const handleOrderClick = () => {
    if (onOrder && product.currentStock > 0) {
      onOrder(product, cantidad);
    }
  };

  const incrementarCantidad = () => {
    if (cantidad < product.currentStock) {
      setCantidad(cantidad + 1);
    }
  };

  const decrementarCantidad = () => {
    if (cantidad > 1) {
      setCantidad(cantidad - 1);
    }
  };

  return (
    <div className={styles.productCard}>
      <div className={styles.productHeader}>
        <h3>{product.name}</h3>
        <span className={styles.price}>{formatPrice(product.price)}</span>
      </div>
      
      {product.description && (
        <p className={styles.productDescription}>{product.description}</p>
      )}
      
      <div className={styles.stockInfo}>
        <span className={`${styles.stockBadge} ${
          product.currentStock > 10 
            ? styles.stockHigh 
            : product.currentStock > 0 
              ? styles.stockMedium 
              : styles.stockLow
        }`}>
          {product.currentStock > 0 
            ? `Disponible (${product.currentStock})` 
            : "Agotado"}
        </span>
      </div>

      {product.currentStock > 0 && (
        <div className={styles.quantitySelector}>
          <label className={styles.quantityLabel}>Cantidad:</label>
          <div className={styles.quantityControls}>
            <button 
              className={styles.quantityBtn}
              onClick={decrementarCantidad}
              disabled={cantidad <= 1}
            >
              -
            </button>
            <span className={styles.quantityValue}>{cantidad}</span>
            <button 
              className={styles.quantityBtn}
              onClick={incrementarCantidad}
              disabled={cantidad >= product.currentStock}
            >
              +
            </button>
          </div>
        </div>
      )}

      <div className={styles.actions}>
        <button 
          className={`${styles.orderButton} ${
            product.currentStock === 0 ? styles.orderButtonDisabled : ''
          }`}
          disabled={product.currentStock === 0}
          onClick={handleOrderClick}
        >
          {product.currentStock > 0 
            ? `Agregar al Carrito ($${(product.price * cantidad).toFixed(2)})` 
            : "No disponible"}
        </button>
      </div>
    </div>
  );
};