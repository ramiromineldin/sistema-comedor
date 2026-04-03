import styles from "../../screens/login/LoginScreen.module.css";
import { NavItem } from './NavItem';
import BotonNav from './BotonNav';


type NavbarProps = {
    titulo: string;
    itemsNav: NavItem[];
    itemStyle: string;
    color: string
    screenName: string;
};

export default function LoginNavbar(props: NavbarProps) {

  return (
    <nav className={styles.navbar} style={{backgroundColor:props.color}}> 
    
        <h2 className={styles.icono}> 
            {props.titulo}
        </h2>
    
        <ul className={styles.navItems}>
            {props.itemsNav.map((item)=> {
                return(
                   <li key={item.path}>
                    <BotonNav 
                    className={`${styles.navItem} ${props.itemStyle}`} 
                    nombre={item.nombre} 
                    ubicacion={item.path}/>
                   </li> 
            )})}
            
            <button className={styles.signup}> 
                {props.screenName}
            </button>
                  
        </ul>
    
    </nav>
  );
}
