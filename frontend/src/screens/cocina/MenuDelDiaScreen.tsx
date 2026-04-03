
import NavBar from "@/components/CommonLayout/NavBar";
import styles from "./PersonalScreen.module.css"
import { useState } from "react";
import Sidebar from "@/components/CommonLayout/Sidebar";
import { navItems } from "@/components/inicio/NavItem";
import { BiSolidDish } from 'react-icons/bi';
import { MenuDelDia } from "@/components/Staff/MenuDelDia";

export const MenuDelDiaScreen = () => {
    const[open, setOpen] = useState(false);
    const itemsNav = navItems.menuDelDia;

    return(
      <div className={styles.background}>
        <NavBar
          titulo={"Menú del día"}
          icon={<BiSolidDish size={32}/>}
          action= {() => setOpen(true)} />
        <Sidebar 
        open={open} 
        closeMenu={() => setOpen(false)} 
        items={itemsNav} place={"Menú del día"} />

        <MenuDelDia/>

      </div>
  );
};

