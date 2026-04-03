import { useQuery } from "@tanstack/react-query";
import { useAccessTokenGetter } from "@/services/TokenContext";

import styles from "./MenuDelDia.module.css";
import { BASE_API_URL } from "@/config/app-query-client";
import { BiSolidDish } from "react-icons/bi";

interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  currentStock: number;
}

export interface MenuDelDia {
  id: number;
  nombre: string;
  descripcion: string;
  fecha: string;
  activo: boolean;
  alimentos: Product[];
}

export const MenuDelDia = () => {
  const getAccessToken = useAccessTokenGetter();

  const { data: menuData, isLoading, error } = useQuery({
    queryKey: ["menu", "hoy"],
    queryFn: async (): Promise<MenuDelDia> => {
      const apiBase = BASE_API_URL;
      const token = await getAccessToken();
      
      const response = await fetch(`${apiBase}/menu/hoy`, {
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

  if (isLoading) {
    return (
      
        <div className={styles.container}>
          <div className={styles.loading}>
            <div className={styles.spinner}></div>
            <p>Cargando menú del día...</p>
          </div>
        </div>
      
    );
  }

  if (error) {
    return (
      
        <div className={styles.container}>
          <div className={styles.error}>
            <h2>Error al cargar el menú</h2>
            <p>{error.message}</p>
            <button 
              className={styles.retryButton}
              onClick={() => window.location.reload()}
            >
              Reintentar
            </button>
          </div>
        </div>
      
    );
  }

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('es-AR', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('es-AR', {
      style: 'currency',
      currency: 'ARS'
    }).format(price);
  };

  return (
    
      <div className={styles.container}>
        <div className={styles.header}>
          
          {menuData && (
            <>
              <p className={styles.date}>Día {formatDate(menuData.fecha)}</p>
              {menuData.descripcion && (
                <p className={styles.description}>{menuData.descripcion}</p>
              )}
            </>
          )}
        </div>

        {menuData && menuData.alimentos && menuData.alimentos.length > 0 ? (
          <div className={styles.menuGrid}>
            {menuData.alimentos.map((product) => (
              <div key={product.id} className={styles.productCard}>
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
                      ? `Stock: ${product.currentStock}` 
                      : "Sin stock"}
                  </span>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className={styles.emptyState}>
            <div className={styles.emptyIcon}><BiSolidDish /></div>
            <h3>No hay productos disponibles</h3>
            <p>El menú de hoy aún no está disponible o no tiene productos asignados.</p>
          </div>
        )}

        
      </div>
    
  );
};