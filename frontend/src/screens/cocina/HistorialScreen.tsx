
import NavBar from "@/components/CommonLayout/NavBar";
import styles from "./PersonalScreen.module.css"
import { useState } from "react";
import Sidebar from "@/components/CommonLayout/Sidebar";
import { navItems } from "@/components/inicio/NavItem";
import { Historial } from "@/components/Staff/Historial";
import { LuHistory} from "react-icons/lu";

export const HistorialScreen = () => {
    const[open, setOpen] = useState(false);
    const itemsNav = navItems.historial;

    return(
      <div className={styles.background}>
        <NavBar
          titulo={"Historial de pedidos"}
          icon={<LuHistory/>}
          action= {() => setOpen(true)} />
        <Sidebar 
        open={open} 
        closeMenu={() => setOpen(false)} 
        items={itemsNav} place={"Historial"} />

        <Historial/>
      </div>
  );
};

