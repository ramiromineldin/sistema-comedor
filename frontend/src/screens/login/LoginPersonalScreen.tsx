import styles from "./LoginScreen.module.css";
import { navItems } from "@/components/inicio/NavItem";
import LoginNavbar from "@/components/inicio/LoginNavbar";
import { Login } from "@/components/Login/Login";


export const LoginPersonalScreen = () => {
    const itemsNav = navItems.comun;
    const color= "#628ECB";
    
        return(
          <div className={styles.fondoPersonal}>
            <LoginNavbar titulo={"Personal del comedor"}
              itemsNav={itemsNav}
              itemStyle={styles.personalItem}
              color={color} screenName={"Ingreso"} />

            <Login rol={"PERSONAL_COCINA"}/>
          </div>
      );
};

