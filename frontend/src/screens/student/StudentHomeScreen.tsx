import layoutStyles from "./StudentHomeScreen.module.css"
import navStyles from "./StudentNavbar.module.css";
import { navItems } from "@/components/inicio/NavItem";
import {StudentHome} from "@/components/student/StudentHome";
import logo from "/imagenes/fiuba-logo.png"
import { CommonScreen } from "../CommonScreen";

export const StudentHomeScreen = () => {

    return(
      <CommonScreen
        title={"Comedor"} 
        navItems={navItems.studentNav}
        layoutStyles={layoutStyles}
        screenName={"Home"}
        children={<StudentHome/>}
        logo={<img src={logo} alt="Logo" />}
        navStyles={navStyles}/>
        
  );
};