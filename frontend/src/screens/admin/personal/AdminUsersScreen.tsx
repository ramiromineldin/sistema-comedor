import layoutStyles from "../AdminScreen.module.css"
import navStyles from "../AdminNavbar.module.css";
import logo from "/imagenes/fiuba-logo.png"
import { CommonScreen } from "@/screens/CommonScreen";
import { navItems } from "@/components/inicio/NavItem";
import { AdminUsers } from "@/components/admin/AdminUsers";


export const AdminUsersScreen = () => {

    return(
      <CommonScreen
        title={"Comedor"} 
        navItems={navItems.personalAdmin}
        layoutStyles={layoutStyles}
        screenName={"Personal de cocina"}
        children={<AdminUsers/>}
        logo={<img src={logo} alt="Logo" />}
        navStyles={navStyles}/>
        
  );
};