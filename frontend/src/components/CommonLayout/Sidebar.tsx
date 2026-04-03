import BotonNav from "../inicio/BotonNav";
import styles from "./Sidebar.module.css";
import { IoClose } from "react-icons/io5";
import { NavItem } from "../inicio/NavItem";
import { useToken } from "@/services/TokenContext";

type SidebarProps= {
    open: boolean;
    closeMenu: () => void;
    items: NavItem[];
    place: string;
};

export default function Sidebar(props: SidebarProps) {
    const [, setTokenState] = useToken();
    const logOut = () => {
        setTokenState({ state: "LOGGED_OUT" });
      };
    return (
      <>
        {props.open && <div className={styles.overlay} onClick={props.closeMenu} />}
  
        <div className={`${styles.sidebar} ${props.open ? styles.open : ""}`}>
            <div className={styles.button}>
                <button className={styles.closeMenu} onClick={props.closeMenu}>
                    <IoClose size={20}/>
                </button>
            </div >

            <div className={styles.bar}>
                <button className={styles.activa}> 
                    {props.place}
                </button>
            

                <ul className={styles.items}>
                    
                    {props.items.map((item)=> {
                        return(

                        <BotonNav 
                                className={styles.item}
                                nombre={item.nombre} 
                                ubicacion={item.path}/>
                            )})}
                    <button className={styles.item} onClick={logOut}> Cerrar sesión </button>
                </ul>
                
                </div>
        </div>
      </>
    );
  }


  