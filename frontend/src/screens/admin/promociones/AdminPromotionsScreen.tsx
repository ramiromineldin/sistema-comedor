import layoutStyles from "../AdminScreen.module.css"
import navStyles from "../AdminNavbar.module.css";
import logo from "/imagenes/fiuba-logo.png"
import { CommonScreen } from "@/screens/CommonScreen";
import { navItems } from "@/components/inicio/NavItem";
import AdminPromotions from "@/components/admin/AdminPromotions";

export const AdminPromotionsScreen = () => {

    return(
      <CommonScreen
        title={"Comedor"} 
        navItems={navItems.promocionesAdmin}
        layoutStyles={layoutStyles}
        screenName={"Promociones"}
        children={<AdminPromotions/>}
        logo={<img src={logo} alt="Logo" />}
        navStyles={navStyles}/>
        
  );
};