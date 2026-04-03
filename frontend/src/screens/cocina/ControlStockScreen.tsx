
import NavBar from "@/components/CommonLayout/NavBar";
import styles from "./PersonalScreen.module.css"
import { useState } from "react";
import Sidebar from "@/components/CommonLayout/Sidebar";
import { navItems } from "@/components/inicio/NavItem";
import {ControlStock} from "@/components/Staff/ControlStock";
import { MdFastfood } from 'react-icons/md';

export const ControlStockScreen = () => {
    const[open, setOpen] = useState(false);
    const itemsNav = navItems.stock;

    return(
      <div className={styles.background}>
        <NavBar
          titulo={"Control de Stock"}
          icon={<MdFastfood size={30}/>}
          action= {() => setOpen(true)} />
        <Sidebar 
        open={open} 
        closeMenu={() => setOpen(false)} 
        items={itemsNav} place={"Control de stock"} />

        <ControlStock/>
      </div>
  );
};

