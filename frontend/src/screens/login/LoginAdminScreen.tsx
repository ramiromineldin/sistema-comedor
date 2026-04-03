import styles from "./LoginScreen.module.css";
import { navItems } from "@/components/inicio/NavItem";
import LoginNavbar from "@/components/inicio/LoginNavbar";
import { Login } from "@/components/Login/Login";

export const LoginAdminScreen = () => {
    const itemsNav = navItems.comun;
    const color = "#395886";

    return(
      <div className={styles.fondoAdmin}>
        <LoginNavbar 
          titulo={"Administrador"}
          itemsNav={itemsNav}
          itemStyle={styles.adminItem}
          color={color} screenName={"Ingreso"} />

        <Login rol={"ADMIN"}/>
      </div>
  );
};

