import layoutStyles from "./AdminScreen.module.css";
import navStyles from "./AdminNavbar.module.css";
import logo from "/imagenes/fiuba-logo.png"
import { CommonScreen } from "@/screens/CommonScreen";
import { navItems } from "@/components/inicio/NavItem";
import { AdminHome } from "@/components/admin/AdminHome";

export const AdminHomeScreen = () => {

    return(
      <CommonScreen
        title={"Comedor"} 
        navItems={navItems.homeAdmin}
        layoutStyles={layoutStyles}
        screenName={"Home"}
        children={<AdminHome/>}
        logo={<img src={logo} alt="Logo" />}
        navStyles={navStyles}/>
        
  );
};