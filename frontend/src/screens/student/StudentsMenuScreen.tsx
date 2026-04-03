
import NavBar from "@/components/CommonLayout/NavBar";
import layoutStyles from "./StudentMenuScreen.module.css"
import navStyles from "./StudentNavbar.module.css";
import { useState } from "react";
import Sidebar from "@/components/CommonLayout/Sidebar";
import { navItems } from "@/components/inicio/NavItem";
import logo from "/public/imagenes/fiuba-logo.png"
import { StudentMenu } from "@/components/student/StudentMenu";

export const StudentMenuScreen = () => {
    const[open, setOpen] = useState(false);
    const itemsNav = navItems.studentNav.filter(item => item.nombre !== "Menú del día");

    return(
      <div className={layoutStyles.background}>
        <NavBar
          titulo={"Comedor"}
          action= {() => setOpen(true)} 
          style={navStyles}
          logo={<img src={logo} alt="Logo" />}
          />
        <Sidebar 
        open={open} 
        closeMenu={() => setOpen(false)} 
        items={itemsNav} place={"Menú del día"} />

        <StudentMenu/>
      </div>
  );
};

