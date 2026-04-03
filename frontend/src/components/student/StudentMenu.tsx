import { useQuery } from "@tanstack/react-query";
import { MenuItemList } from "@/components/MenuItemList";
import { CarritoFloating } from "@/components/CarritoFloating/CarritoFloating";
import { useAccessTokenGetter } from "@/services/TokenContext";
import { useCarrito } from "@/hooks/useCarrito";
import { BiSolidDish } from 'react-icons/bi';
import { MdLocalOffer } from 'react-icons/md';
import { buildApiUrl } from '@/config/env-config';
import styles from "./StudentMenu.module.css";

interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  currentStock: number;
}

interface Combo {
  id: number;
  name: string;
  description: string;
  price: number;
  currentStock: number;
  subAlimentos: Product[];
}

interface MenuDelDia {
  id: number;
  nombre: string;
  descripcion: string;
  fecha: string;
  activo: boolean;
  alimentos: Product[];
  combos: Combo[];
}

interface Promotion {
  id: number;
  name: string;
  type: string;
  active: boolean;
  discountPercentage?: number;
  discountAmount?: number;
  minimumPurchaseAmount?: number;
}

export const StudentMenu = () => {
  const getAccessToken = useAccessTokenGetter();
  const carrito = useCarrito();

  const { data: menuData, isLoading, error } = useQuery({
    queryKey: ["menu", "hoy"],
    queryFn: async (): Promise<MenuDelDia> => {
      const token = await getAccessToken();
      
      const response = await fetch(buildApiUrl("/menu/hoy"), {
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

  const { data: promotions = [] } = useQuery({
    queryKey: ["promotions"],
    queryFn: async (): Promise<Promotion[]> => {
      const token = await getAccessToken();
      const response = await fetch(buildApiUrl("/promotions"), {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });
      if (!response.ok) throw new Error('Error al cargar promociones');
      return response.json();
    },
  });

  // Obtener promociones de monto mínimo activas
  const promosMonto = promotions.filter(p => 
    p.active && 
    (p.type === 'AMOUNT_DISCOUNT' || p.type === 'PERCENTAGE_DISCOUNT') &&
    p.minimumPurchaseAmount
  ).sort((a, b) => (a.minimumPurchaseAmount || 0) - (b.minimumPurchaseAmount || 0));

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

  return (
      <div className={styles.container}>
          <div className={styles.itemContainer}>
          
          {/* Cuadro de promociones en esquina superior derecha */}
          {promosMonto.length > 0 && (
            <div style={{
              position: 'fixed',
              top: '80px',
              right: '20px',
              width: '220px',
              backgroundColor: '#ffffff',
              border: '2px solid #4caf50',
              borderRadius: '10px',
              padding: '10px',
              boxShadow: '0 4px 12px rgba(0,0,0,0.15)',
              zIndex: 100,
              maxHeight: 'calc(100vh - 120px)',
              overflowY: 'auto'
            }}>
              <div style={{
                display: 'flex',
                alignItems: 'center',
                gap: '4px',
                marginBottom: '8px',
                color: '#2e7d32',
                fontSize: '0.85rem',
                fontWeight: 'bold',
                borderBottom: '1px solid #e8f5e9',
                paddingBottom: '4px'
              }}>
                <MdLocalOffer size={16} />
                <span>Promociones</span>
              </div>
              {promosMonto.map((promo) => (
                <div 
                  key={promo.id}
                  style={{
                    backgroundColor: '#e8f5e9',
                    padding: '8px',
                    borderRadius: '6px',
                    marginBottom: '6px',
                    border: '1px solid #a5d6a7'
                  }}
                >
                  <div style={{ 
                    color: '#1b5e20',
                    fontWeight: 'bold',
                    fontSize: '0.75rem',
                    lineHeight: '1.3'
                  }}>
                    {promo.name}
                  </div>
                </div>
              ))}
            </div>
          )}
          
          <div className={styles.header}>
          <h1><BiSolidDish className={styles.headerIcon} /> Menú del Día</h1>
          {menuData && (
            <div className={styles.headerRight}>
              
              <p className={styles.date}>{formatDate(menuData.fecha)}</p>
              {menuData.descripcion && (
                <p className={styles.description}>{menuData.descripcion}</p>
              )}
            </div>
          )}
        </div>
            <MenuItemList 
            products={menuData?.alimentos || []} 
            onOrderItem={carrito.handleOrderItem} 
            />

            {menuData?.combos && menuData.combos.length > 0 && (
              <div className={styles.combosSection}>
                <h2 className={styles.sectionTitle}>
                  <BiSolidDish className={styles.headerIcon} /> Combos Especiales
                </h2>
                <MenuItemList 
                  products={menuData.combos} 
                  onOrderItem={carrito.handleOrderItem} 
                />
              </div>
            )}

            <CarritoFloating carrito={carrito} />

        </div>
      </div>
    
  );
};