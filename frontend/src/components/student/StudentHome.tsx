
import { useLocation } from "wouter";
import { BiSolidDish } from "react-icons/bi";
import { MdFastfood } from "react-icons/md";
import styles from "./StudentHome.module.css";

export const StudentHome = () => {
  const [, setLocation] = useLocation();

  const handleMenuClick = () => {
    setLocation("/estudiante/menu-del-dia");
  };

  const handleCombosClick = () => {
    setLocation("/estudiante/combos-especiales");
  }

  return (
    
      <div className={styles.container}>
        <h2>¡Bienvenido! Aquí puedes ver el menú disponible para hoy</h2>

        <div className={styles.grid}>
          
         
          <div className={`${styles.card} ${styles.cardMenu}`} onClick={handleMenuClick}>
            <BiSolidDish className={styles.icon} />
            <h3>Menú del Día</h3>
            <p>Ver todos los platos disponibles para hoy</p>
          </div>

          
          <div style={{
            padding: "20px",
            border: "1px solid #ddd",
            borderRadius: "8px",
            backgroundColor: "#e67e22",
            color: "white",
            cursor: "pointer",
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            gap: "10px"
          }}
            onClick={handleCombosClick}
            onMouseEnter={(e) => {
              e.currentTarget.style.transform = "translateY(-2px)";
              e.currentTarget.style.boxShadow = "0 4px 12px rgba(52, 152, 219, 0.3)";
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.transform = "translateY(0)";
              e.currentTarget.style.boxShadow = "none";
            }}>
            <MdFastfood style={{ fontSize: "2rem" }} />
            <h3 style={{ margin: "0", fontSize: "1.3rem" }}>Combos Especiales</h3>
            <p style={{ margin: "0", opacity: "0.9" }}>Descubre nuestros combos con descuentos</p>
          </div>
        </div>
      </div>
    
  );
};
