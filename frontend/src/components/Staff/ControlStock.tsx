import { useQuery } from "@tanstack/react-query";
import { useAccessTokenGetter } from "@/services/TokenContext";
import { GiCarrot } from 'react-icons/gi';
import { BiSolidDish } from 'react-icons/bi';
import { MdFastfood } from 'react-icons/md';
import styles from "./ControlStock.module.css";
import { BASE_API_URL } from "@/config/app-query-client";

interface Ingredient {
  id: number;
  name: string;
  description: string;
  stock: number;
  price: number;
}

interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  available: boolean;
  currentStock: number;
  hasStock: boolean;
}

interface Combo {
  id: number;
  name: string;
  description: string;
  price: number;
  discount: number;
  available: boolean;
  tipo: string;
  subAlimentos: Array<{
    id: number;
    name: string;
    tipo: string;
    price: number;
  }>;
}

interface AlimentoDisplay {
  id: number;
  name: string;
  description: string;
  stock: number;
  available?: boolean;
  type: 'ingredient' | 'product' | 'combo';
}

export const ControlStock = () => {
  const getAccessToken = useAccessTokenGetter();

  const { data: ingredients, isLoading: isLoadingIngredients, error: errorIngredients } = useQuery({
    queryKey: ["ingredients", "all"],
    queryFn: async (): Promise<Ingredient[]> => {
      const apiBase = BASE_API_URL;

      const token = await getAccessToken();
      
      const response = await fetch(`${apiBase}/ingredients`, {
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

  const { data: products, isLoading: isLoadingProducts, error: errorProducts } = useQuery({
    queryKey: ["products", "all"],
    queryFn: async (): Promise<Product[]> => {
      const apiBase = BASE_API_URL;
      const token = await getAccessToken();
      
      const response = await fetch(`${apiBase}/products`, {
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

  const { data: combos, isLoading: isLoadingCombos, error: errorCombos } = useQuery({
    queryKey: ["combos", "all"],
    queryFn: async (): Promise<Combo[]> => {
      const apiBase = BASE_API_URL;
      const token = await getAccessToken();
      
      const response = await fetch(`${apiBase}/admin/combos`, {
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

  const isLoading = isLoadingIngredients || isLoadingProducts || isLoadingCombos;
  const error = errorIngredients || errorProducts || errorCombos;

  const alimentos: AlimentoDisplay[] = [
    ...(ingredients?.map(ing => ({
      id: ing.id,
      name: ing.name,
      description: ing.description,
      stock: ing.stock,
      type: 'ingredient' as const
    })) || []),
    ...(products?.map(prod => ({
      id: prod.id,
      name: prod.name,
      description: prod.description,
      stock: prod.currentStock,
      type: 'product' as const
    })) || []),
    ...(combos?.map(combo => ({
      id: combo.id,
      name: combo.name,
      description: combo.description,
      stock: combo.available ? 1 : 0, 
      available: combo.available,
      type: 'combo' as const
    })) || [])
  ];

  if (isLoading) {
    return (
      
        <div className={styles.container}>
          <div className={styles.loading}>
            <div className={styles.spinner}></div>
            <p>Cargando inventario...</p>
          </div>
        </div>
      
    );
  }

  if (error) {
    return (
      
        <div className={styles.container}>
          <div className={styles.error}>
            <h2>Error al cargar el inventario</h2>
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

  const getStockStatus = (stock: number, isCombo?: boolean) => {
    if (isCombo) {
      return stock === 0 ? 'sin-stock' : 'stock-alto';
    }
    if (stock === 0) return 'sin-stock';
    if (stock <= 5) return 'stock-bajo';
    if (stock <= 20) return 'stock-medio';
    return 'stock-alto';
  };

  const getStockLabel = (stock: number, isCombo?: boolean) => {
    if (isCombo) {
      return stock === 0 ? 'NO DISPONIBLE' : 'DISPONIBLE';
    }
    if (stock === 0) return 'SIN STOCK';
    if (stock <= 5) return 'STOCK BAJO';
    if (stock <= 20) return 'STOCK MEDIO';
    return 'STOCK ALTO';
  };

  // Agrupar por tipo
  const groupedAlimentos = {
    'INGREDIENTES': alimentos.filter(a => a.type === 'ingredient'),
    'PRODUCTOS': alimentos.filter(a => a.type === 'product'),
    'COMBOS': alimentos.filter(a => a.type === 'combo')
  };

  return (
      <div className={styles.container}>
        <div className={styles.header}>
          <p className={styles.subtitle}>
            Monitoreo del inventario de alimentos del comedor
          </p>
        </div>

        {Object.keys(groupedAlimentos).length > 0 ? (
          <div className={styles.sections}>
            {Object.entries(groupedAlimentos).map(([tipo, items]) => (
              <div key={tipo} className={styles.section}>
                <div className={styles.sectionHeader}>
                  <h2>
                    {tipo === 'INGREDIENTES' ? (
                      <>
                        <GiCarrot className={styles.sectionIcon} /> Ingredientes
                      </>
                    ) : tipo === 'PRODUCTOS' ? (
                      <>
                        <BiSolidDish className={styles.sectionIcon} /> Productos
                      </>
                    ) : (
                      <>
                        <MdFastfood className={styles.sectionIcon} /> Combos
                      </>
                    )}
                  </h2>
                  <span className={styles.count}>{items.length} items</span>
                </div>
                
                <div className={styles.itemsGrid}>
                  {items.map((alimento) => (
                    <div key={alimento.id} className={styles.itemCard}>
                      <div className={styles.itemHeader}>
                        <div className={styles.titleSection}>
                          <span className={styles.typeIcon}>
                            {alimento.type === 'ingredient' ? (
                              <GiCarrot />
                            ) : alimento.type === 'product' ? (
                              <BiSolidDish />
                            ) : (
                              <MdFastfood />
                            )}
                          </span>
                          <h3>{alimento.name}</h3>
                        </div>
                        <span 
                          className={`${styles.stockBadge} ${styles[getStockStatus(alimento.stock, alimento.type === 'combo')]}`}
                        >
                          {getStockLabel(alimento.stock, alimento.type === 'combo')}
                        </span>
                      </div>
                      
                      {alimento.description && (
                        <p className={styles.itemDescription}>{alimento.description}</p>
                      )}
                      
                      <div className={styles.stockDetails}>
                        <div className={styles.stockNumber}>
                          <span className={styles.stockLabel}>
                            {alimento.type === 'combo' ? 'Estado:' : 'Cantidad disponible:'}
                          </span>
                          <span className={styles.stockValue}>
                            {alimento.type === 'combo' 
                              ? (alimento.available ? 'Disponible' : 'No disponible')
                              : `${alimento.stock} unidades`
                            }
                          </span>
                        </div>
                        
                        {alimento.type !== 'combo' && (
                          <div className={styles.stockProgress}>
                            <div 
                              className={`${styles.progressBar} ${styles[getStockStatus(alimento.stock, false)]}`}
                              style={{ 
                                width: `${Math.min((alimento.stock / 50) * 100, 100)}%` 
                              }}
                            />
                          </div>
                        )}
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className={styles.emptyState}>
            <div className={styles.emptyIcon}>
              <MdFastfood />
            </div>
            <h3>No hay alimentos registrados</h3>
            <p>No se encontraron alimentos en el inventario.</p>
          </div>
        )}

        {/* Resumen de stock */}
        {alimentos && alimentos.length > 0 && (
          <div className={styles.summary}>
            <h3>
              <MdFastfood className={styles.summaryIcon} /> Resumen de Stock
            </h3>
            <div className={styles.summaryStats}>
              <div className={styles.stat}>
                <span className={styles.statValue}>{alimentos.length}</span>
                <span className={styles.statLabel}>Total Items</span>
              </div>
              <div className={styles.stat}>
                <span className={styles.statValue}>
                  {alimentos.filter(a => a.stock === 0).length}
                </span>
                <span className={styles.statLabel}>Sin Stock/No Disponible</span>
              </div>
              <div className={styles.stat}>
                <span className={styles.statValue}>
                  {alimentos.filter(a => a.type !== 'combo' && a.stock > 0 && a.stock <= 5).length}
                </span>
                <span className={styles.statLabel}>Stock Bajo</span>
              </div>
              <div className={styles.stat}>
                <span className={styles.statValue}>
                  {alimentos.filter(a => 
                    (a.type === 'combo' && a.stock > 0) || 
                    (a.type !== 'combo' && a.stock > 5)
                  ).length}
                </span>
                <span className={styles.statLabel}>Disponible/Stock OK</span>
              </div>
            </div>
          </div>
        )}

  
      </div>
    
  );
};