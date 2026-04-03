import NavBar from "@/components/CommonLayout/NavBar";
import Sidebar from "@/components/CommonLayout/Sidebar";
import { navItems } from "@/components/inicio/NavItem";
import logo from "/public/imagenes/fiuba-logo.png";
import layoutStyles from "../StudentMenuScreen.module.css";
import navStyles from "../StudentNavbar.module.css";
import { useState } from 'react';
import { CarritoFloating } from '@/components/CarritoFloating/CarritoFloating';
import { useCarrito } from '@/hooks/useCarrito';
import { useQuery } from "@tanstack/react-query";
import { useAccessTokenGetter } from "@/services/TokenContext";
import { buildApiUrl } from '@/config/env-config';
import { MdAccessTime, MdCalendarToday } from 'react-icons/md';

interface Product {
    id: number;
    name: string;
    description: string;
    price: number;
    currentStock: number;
}

interface PromotionItem {
    id: number;
    productId: number;
    productName: string;
    originalPrice: number;
}

interface Promotion {
    id: number;
    name: string;
    description: string;
    startDate: string;
    endDate: string;
    active: boolean;
    type: string;
    originalPrice: number;
    finalPrice: number;
    discountPercentage?: number;
    discountAmount?: number;
    minimumPurchaseAmount?: number;
    buyQuantity?: number;
    payQuantity?: number;
    recurringDay?: string;
    startTime?: string;
    endTime?: string;
    available: boolean;
    items: PromotionItem[];
}

const getDayNameInSpanish = (day: string): string => {
    const dayNames: Record<string, string> = {
        MONDAY: "Lunes",
        TUESDAY: "Martes",
        WEDNESDAY: "Miércoles",
        THURSDAY: "Jueves",
        FRIDAY: "Viernes",
        SATURDAY: "Sábado",
        SUNDAY: "Domingo",
    };
    return dayNames[day] || day;
};

const getPromotionDescription = (promo: Promotion): string => {
    switch (promo.type) {
        case "PERCENTAGE_DISCOUNT":
            return `${promo.discountPercentage}% de descuento`;
        case "RECURRING_DAY_DISCOUNT": {
            const dayName = promo.recurringDay
                ? getDayNameInSpanish(promo.recurringDay)
                : "";
            return `${promo.discountPercentage}% de descuento todos los ${dayName}`;
        }
        case "AMOUNT_DISCOUNT":
            return `$${promo.discountAmount} OFF en compras mayores a $${promo.minimumPurchaseAmount}`;
        case "BUY_X_PAY_Y":
            return `Lleva ${promo.buyQuantity}, paga ${promo.payQuantity}`;
        case "TIME_RESTRICTED_BUY_X_PAY_Y": {
            const horario = promo.startTime && promo.endTime 
                ? ` (${promo.startTime.slice(0, 5)} - ${promo.endTime.slice(0, 5)})`
                : "";
            return `Lleva ${promo.buyQuantity}, paga ${promo.payQuantity}${horario}`;
        }
        default:
            return "";
    }
};


export function DiscountScreen(){
    const [open, setOpen] = useState(false);
    const itemsNav = navItems.promotionsStudent;
    const carrito = useCarrito();
    const getAccessToken = useAccessTokenGetter();

    const { data: promotionsData, isLoading: loadingPromotions, error: errorPromotions } = useQuery({
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

    const { isLoading: loadingProducts, error: errorProducts } = useQuery({
        queryKey: ["products"],
        queryFn: async (): Promise<Product[]> => {
            const token = await getAccessToken();
            const response = await fetch(buildApiUrl("/products"), {
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

    const loading = loadingPromotions || loadingProducts;
    const error = errorPromotions || errorProducts;

    if (loading) {
        return (
            <div className={layoutStyles.background}>
                <NavBar
                    titulo={"Comedor"}
                    action={() => setOpen(true)} 
                    style={navStyles}
                    logo={<img src={logo} alt="Logo" />}
                />
                <Sidebar 
                    open={open} 
                    closeMenu={() => setOpen(false)} 
                    items={itemsNav} 
                    place={"Combos Especiales"} 
                />
                <div style={{ textAlign: 'center', padding: '40px' }}>
                    <p>Cargando promociones...</p>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className={layoutStyles.background}>
                <NavBar
                    titulo={"Comedor"}
                    action={() => setOpen(true)} 
                    style={navStyles}
                    logo={<img src={logo} alt="Logo" />}
                />
                <Sidebar 
                    open={open} 
                    closeMenu={() => setOpen(false)} 
                    items={itemsNav} 
                    place={"Combos Especiales"} 
                />
                <div style={{ textAlign: 'center', padding: '40px' }}>
                    <h2>Error al cargar promociones</h2>
                    <p>{error instanceof Error ? error.message : String(error)}</p>
                </div>
            </div>
        );
    }

    const activePromotions = (promotionsData ?? []).filter(p => p.active && p.type !== "AMOUNT_DISCOUNT");

    return(
        <div className={layoutStyles.background}>
            <NavBar
                titulo={"Comedor"}
                action={() => setOpen(true)} 
                style={navStyles}
                logo={<img src={logo} alt="Logo" />}
            />
            <Sidebar 
                open={open} 
                closeMenu={() => setOpen(false)} 
                items={itemsNav} 
                place={"Combos Especiales"} 
            />
            
            <div style={{ padding: '20px' }}>    
                <h1>Promociones Especiales</h1>
                <p style={{ marginBottom: '30px', color: '#666' }}>
                    Aprovechá nuestras promociones activas
                </p>
                
                {activePromotions.length === 0 ? (
                    <div style={{ 
                        textAlign: 'center', 
                        padding: '40px',
                        backgroundColor: '#f8f9fa',
                        borderRadius: '8px'
                    }}>
                        <p>No hay promociones activas en este momento</p>
                    </div>
                ) : (
                    <div style={{
                        display: 'grid',
                        gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))',
                        gap: '20px',
                        marginBottom: '100px'
                    }}>
                        {activePromotions.map((promo) => (
                            <div 
                                key={promo.id}
                                style={{
                                    border: promo.available ? '2px solid #3498db' : '2px solid #95a5a6',
                                    borderRadius: '12px',
                                    padding: '20px',
                                    backgroundColor: promo.available ? 'white' : '#f8f9fa',
                                    boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
                                    opacity: promo.available ? 1 : 0.7,
                                }}
                            >
                                {!promo.available && (
                                    <div style={{
                                        backgroundColor: '#e74c3c',
                                        color: 'white',
                                        padding: '8px 12px',
                                        borderRadius: '6px',
                                        fontSize: '0.85rem',
                                        fontWeight: 'bold',
                                        marginBottom: '10px',
                                        textAlign: 'center',
                                        display: 'flex',
                                        alignItems: 'center',
                                        justifyContent: 'center',
                                        gap: '6px'
                                    }}>
                                        {promo.type === 'TIME_RESTRICTED_BUY_X_PAY_Y' ? (
                                            <>
                                                <MdAccessTime size={16} />
                                                <span>Fuera de horario</span>
                                            </>
                                        ) : (
                                            <>
                                                <MdCalendarToday size={16} />
                                                <span>No disponible hoy</span>
                                            </>
                                        )}
                                    </div>
                                )}
                                
                                <h3 style={{ 
                                    color: '#2c3e50', 
                                    marginTop: 0,
                                    marginBottom: '10px',
                                    fontSize: '1.3rem'
                                }}>
                                    {promo.name}
                                </h3>
                                
                                <div style={{
                                    backgroundColor: '#e8f4f8',
                                    color: '#2c3e50',
                                    padding: '10px',
                                    borderRadius: '8px',
                                    fontSize: '1rem',
                                    fontWeight: 'bold',
                                    marginBottom: '15px',
                                    textAlign: 'center',
                                    border: '1px solid #3498db'
                                }}>
                                    {getPromotionDescription(promo)}
                                </div>

                                <div style={{
                                    display: 'flex',
                                    justifyContent: 'space-between',
                                    alignItems: 'center',
                                    marginBottom: '15px',
                                    padding: '12px',
                                    backgroundColor: '#f8f9fa',
                                    borderRadius: '8px'
                                }}>
                                    <div>
                                        <span style={{ 
                                            fontSize: '0.85rem',
                                            color: '#666',
                                            display: 'block'
                                        }}>
                                            Precio original:
                                        </span>
                                        <span style={{ 
                                            fontSize: '1.1rem',
                                            color: '#999',
                                            textDecoration: 'line-through'
                                        }}>
                                            ${promo.originalPrice.toFixed(2)}
                                        </span>
                                    </div>
                                    <div style={{ textAlign: 'right' }}>
                                        <span style={{ 
                                            fontSize: '0.85rem',
                                            color: '#666',
                                            display: 'block'
                                        }}>
                                            Precio final:
                                        </span>
                                        <span style={{ 
                                            fontSize: '1.4rem',
                                            color: '#27ae60',
                                            fontWeight: 'bold'
                                        }}>
                                            ${(promo.finalPrice || 0).toFixed(2)}
                                        </span>
                                    </div>
                                </div>

                                <p style={{ 
                                    color: '#555',
                                    fontSize: '0.9rem',
                                    marginBottom: '15px',
                                    lineHeight: '1.4'
                                }}>
                                    {promo.description}
                                </p>

                                <div style={{ 
                                    borderTop: '1px solid #eee',
                                    paddingTop: '15px',
                                    marginTop: '15px'
                                }}>
                                    <h4 style={{ 
                                        fontSize: '0.95rem',
                                        color: '#34495e',
                                        marginBottom: '10px',
                                        fontWeight: '600'
                                    }}>
                                        Productos incluidos:
                                    </h4>
                                    <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', marginBottom: '15px' }}>
                                        {promo.items.map((item) => (
                                            <div 
                                                key={item.id}
                                                style={{
                                                    padding: '10px',
                                                    backgroundColor: '#f8f9fa',
                                                    borderRadius: '6px',
                                                    fontSize: '0.9rem'
                                                }}
                                            >
                                                <span style={{ fontWeight: '500', color: '#2c3e50' }}>
                                                    • {item.productName}
                                                </span>
                                            </div>
                                        ))}
                                    </div>
                                    
                                    <button
                                        onClick={() => {
                                            if (!promo.available) return;
                                            
                                            // Verificar si ya hay una promoción en el carrito
                                            const tienePromocion = carrito.carrito.some(item => item.type === 'PROMOTION');
                                            
                                            if (tienePromocion) {
                                                return;
                                            }
                                            
                                            // Agregar la promoción completa
                                            carrito.agregarPromocion({
                                                id: promo.id,
                                                name: promo.name,
                                                finalPrice: promo.finalPrice
                                            }, 1);
                                        }}
                                        disabled={!promo.available}
                                        style={{
                                            backgroundColor: promo.available ? '#27ae60' : '#95a5a6',
                                            color: 'white',
                                            border: 'none',
                                            padding: '12px',
                                            borderRadius: '6px',
                                            cursor: promo.available ? 'pointer' : 'not-allowed',
                                            fontSize: '1rem',
                                            fontWeight: 'bold',
                                            width: '100%',
                                            opacity: promo.available ? 1 : 0.6
                                        }}
                                    >
                                        {promo.available ? 'Agregar al carrito' : 'No disponible'}
                                    </button>
                                </div>

                                {promo.type !== "RECURRING_DAY_DISCOUNT" && (
                                    <div style={{
                                        marginTop: '15px',
                                        fontSize: '0.85rem',
                                        color: '#7f8c8d',
                                        textAlign: 'center'
                                    }}>
                                        Válido hasta: {new Date(promo.endDate).toLocaleDateString('es-AR')}
                                    </div>
                                )}
                            </div>
                        ))}
                    </div>
                )}
            </div>
            
            <CarritoFloating carrito={carrito} />
        </div>
    );
}
