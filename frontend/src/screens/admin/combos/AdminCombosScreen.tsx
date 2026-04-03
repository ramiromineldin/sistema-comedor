import layoutStyles from "../AdminScreen.module.css"
import navStyles from "../AdminNavbar.module.css";
import logo from "/imagenes/fiuba-logo.png"
import { CommonScreen } from "@/screens/CommonScreen";
import { AdminCombos } from "@/components/admin/AdminCombos";
import { navItems } from "@/components/inicio/NavItem";


export const AdminCombosScreen = () => {

    return(
      <CommonScreen
        title={"Comedor"} 
        navItems={navItems.combossAdmin}
        layoutStyles={layoutStyles}
        screenName={"Combos"}
        children={<AdminCombos/>}
        logo={<img src={logo} alt="Logo" />}
        navStyles={navStyles}/>
        
  );
};