import layoutStyles from "../AdminScreen.module.css"
import navStyles from "../AdminNavbar.module.css";
import logo from "/imagenes/fiuba-logo.png"
import { CommonScreen } from "@/screens/CommonScreen";
import { navItems } from "@/components/inicio/NavItem";
import { AdminProducts } from "@/components/admin/AdminProducts";


export const AdminProductsScreen = () => {

    return(
      <CommonScreen
        title={"Comedor"} 
        navItems={navItems.productosAdmin}
        layoutStyles={layoutStyles}
        screenName={"Productos"}
        children={<AdminProducts/>}
        logo={<img src={logo} alt="Logo" />}
        navStyles={navStyles}/>
        
  );
};