import { useLocation } from "wouter";
import styles from "./AdminHome.module.css";


export const AdminHome = () => {
const [, setLocation] = useLocation();


const cards = [
{ title: "Ingredientes", description: "Gestionar ingredientes", path: "/admin/ingredientes" },
{ title: "Productos", description: "Administrar productos", path: "/admin/productos" },
{ title: "Combos", description: "Crear y editar combos", path: "/admin/combos" },
{ title: "Promociones", description: "Gestionar promociones y descuentos", path: "/admin/promociones" },
{ title: "Personal de Cocina", description: "Gestionar personal", path: "/admin/personal-cocina" },
];


return (
<div className={styles.container}>
<div className={styles.header}>
<h1>Panel de Administración</h1>
</div>


<p>Seleccioná una sección para administrar</p>


<div className={styles.grid}>
{cards.map((card) => (
<div
key={card.title}
className={styles.card}
onClick={() => setLocation(card.path)}
>
<h3>{card.title}</h3>
<p>{card.description}</p>
</div>
))}
</div>
</div>
);
};