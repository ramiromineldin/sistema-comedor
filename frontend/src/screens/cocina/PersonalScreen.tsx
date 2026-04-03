
import NavBar from "@/components/CommonLayout/NavBar";
import styles from "./PersonalScreen.module.css"
import { useState } from "react";
import Sidebar from "@/components/CommonLayout/Sidebar";
import { navItems } from "@/components/inicio/NavItem";
import Pedidos from "@/components/Staff/Pedidos";
import { IoReceipt } from "react-icons/io5";

export const PedidosScreen = () => {
    const[open, setOpen] = useState(false);
    const itemsNav = navItems.pedidos;

    return(
      <div className={styles.background}>
        <NavBar
          titulo={"Pedidos"}
          icon={<IoReceipt/>}
          action= {() => setOpen(true)} />
        <Sidebar 
        open={open} 
        closeMenu={() => setOpen(false)} 
        items={itemsNav} place={"Pedidos activos"} />

        <Pedidos></Pedidos>
      </div>
  );
};

