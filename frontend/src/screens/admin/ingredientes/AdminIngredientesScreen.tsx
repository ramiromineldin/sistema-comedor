import layoutStyles from "../AdminScreen.module.css"
import navStyles from "../AdminNavbar.module.css";
import logo from "/imagenes/fiuba-logo.png"
import { CommonScreen } from "@/screens/CommonScreen";
import { navItems } from "@/components/inicio/NavItem";
import { AdminIngredients } from "@/components/admin/AdminIngredients";


export const AdminIngredientsScreen = () => {

    return(
      <CommonScreen
        title={"Comedor"} 
        navItems={navItems.ingredientesAdmin}
        layoutStyles={layoutStyles}
        screenName={"Ingredientes"}
        children={<AdminIngredients/>}
        logo={<img src={logo} alt="Logo" />}
        navStyles={navStyles}/>
        
  );
};