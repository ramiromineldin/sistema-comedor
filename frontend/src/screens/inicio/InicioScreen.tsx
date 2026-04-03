import BotonNav from "@/components/inicio/BotonNav";
import styles from "./InicioScreen.module.css";

export default function InicioScreen() {
  return (
    <div className={styles.root}>
      <div className={styles.barra}>
        <h2>¿Cómo deseas ingresar?</h2>
      </div>
        <div>
            <div className={styles.arriba}>
                <BotonNav  nombre="Estudiante" 
                ubicacion="login-estudiante" 
                className={styles.estudiante}/>
                
            </div>
            <div className={styles.abajo}>

              <BotonNav  nombre="Personal del comedor" 
                ubicacion="login-personal" 
                className={styles.cocina}/>

                <BotonNav  nombre="Administrador" 
                ubicacion="login-admin" 
                className={styles.admin}/>
                
            </div>
        </div>
    </div>
  );
}
