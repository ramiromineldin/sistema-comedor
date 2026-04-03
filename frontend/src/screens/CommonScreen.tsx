
import NavBar from "@/components/CommonLayout/NavBar";
import navStyles from "./student/StudentNavbar.module.css";
import { ReactNode, useState } from "react";
import Sidebar from "@/components/CommonLayout/Sidebar";
import { NavItem } from "@/components/inicio/NavItem";



type CommonScreenProps = {
    title: string;
    navItems: NavItem[];
    layoutStyles: Record<string,string>;
    navStyles?: Record<string,string>;
    icon?: ReactNode;
    logo?: ReactNode;
    screenName: string;
    children: ReactNode;

};

export const CommonScreen = (props: CommonScreenProps) => {
    const[open, setOpen] = useState(false);
    
    
    const filteredItems = props.navItems.filter(item => item.nombre !== props.screenName);
    
    return(
      <div className={props.layoutStyles.background}>
        <NavBar
          titulo={props.title}
          action= {() => setOpen(true)} 
          style={props.navStyles ? props.navStyles : navStyles}
          icon={props.icon}
          logo={props.logo}
          />
        <Sidebar 
        open={open} 
        closeMenu={() => setOpen(false)} 
        items={filteredItems} 
        place={props.screenName} />

        {props.children}
      </div>
  );
};

