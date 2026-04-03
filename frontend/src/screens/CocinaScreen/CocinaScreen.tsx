import { CommonLayout } from "@/components/CommonLayout/CommonLayout";
import { useLocation } from "wouter";
import styles from "./CocinaScreen.module.css";

export const CocinaScreenDashboard = () => {
  const [, setLocation] = useLocation();

  const cards = [
    {
      title: "Gestión de Pedidos",
      description: "Ver y gestionar pedidos de estudiantes",
      route: "/cocina/pedidos",
    },
    {
      title: "Control de Stock",
      description: "Monitorear el stock de alimentos",
      route: "/cocina/control-stock",
    },
    {
      title: "Menú del Día",
      description: "Ver los productos disponibles para hoy",
      route: "/cocina/menu-del-dia",
    },
    {
      title: "Alertas",
      description: "Ver alertas de stock bajo y otros avisos",
      route: "/cocina/alertas",
    },
  ];

  return (
    <CommonLayout>
      <div className={styles.container}>
        <h1>Dashboard de Cocina</h1>
        <p>Bienvenido al panel de control de la cocina</p>

        <div className={styles.grid}>
          {cards.map((card) => (
            <div
              key={card.title}
              className={styles.card}
              onClick={() => setLocation(card.route)}
            >
              <h3>{card.title}</h3>
              <p>{card.description}</p>
            </div>
          ))}
        </div>
      </div>
    </CommonLayout>
  );
};