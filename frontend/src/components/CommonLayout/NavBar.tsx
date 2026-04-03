import { ReactNode } from "react";
import styles from "./NavBar.module.css";
import { HiMenu } from "react-icons/hi";


type NavBarProps = {
    titulo: string;
    action: () => void;
    icon?: ReactNode;
    style?: Record<string,string>;
    logo?: ReactNode ;
};

export default function NavBar(props: NavBarProps) {
  
  if(props.style){
    return (
      <div className={props.style.bar}>
        <div className={props.style.menu}>
          <button className={props.style.menuButton} onClick={props.action}>
            <HiMenu size={26}/>
          </button>
        </div>
        <div className={props.style.container}>
          {props.icon}
          <h2 className={props.style.titulo}>{props.titulo}</h2>
          <div className={props.style.logo}>
          {props.logo}
        </div>
        </div>
        
  
      </div>
    );
  }

  return (
    <div className={styles.bar}>
      <div className={styles.menu}>
        <button className={styles.menuButton} onClick={props.action}>
          <HiMenu size={20}/>
        </button>
      </div>
      <div className={styles.container}>
        {props.icon}
        <h2 className={styles.titulo}>{props.titulo}</h2>
      </div>
      <div className={styles.placeholder}></div>

    </div>
  );
}
