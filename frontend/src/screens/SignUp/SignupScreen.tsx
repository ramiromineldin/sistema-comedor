import LoginNavbar from "@/components/inicio/LoginNavbar";
import { navItems } from "@/components/inicio/NavItem";
import { SignUp } from "@/components/SignUp/SignUp";
import styles from "./SignUpScreen.module.css";

export const SignupScreen = () => {
  
  const itemsNav = navItems.registro;
  const color = "#395886";

  return (
    <div className={styles.screen}>
      
        <LoginNavbar 
          titulo={"Registro"}
          itemsNav={itemsNav}
          itemStyle={styles.signUpItem}
          color={color} screenName={"Registro"} 
        />
      
      <div className={styles.formBox}>
        <SignUp/>
      </div>
        
    </div>
    
  );
};
