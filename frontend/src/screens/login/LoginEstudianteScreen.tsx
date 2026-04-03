import styles from "./LoginScreen.module.css";
import { navItems } from "@/components/inicio/NavItem";
import LoginNavbar from "@/components/inicio/LoginNavbar";
import { Login } from "@/components/Login/Login";


export const LoginEstudianteScreen = () => {
    const itemsNav = navItems.estudiante;
    const color= "#6AAEE0";
    
        return(
          <div className={styles.fondoEstudiante}>
            <LoginNavbar 
              titulo={"Estudiante"}
              itemsNav={itemsNav}
              itemStyle={styles.estudianteItem}
              color={color} screenName={"Ingreso"} />

            <Login rol={"USER"}/>
          </div>
      );
};

