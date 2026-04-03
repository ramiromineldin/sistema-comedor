import { useAppForm } from "@/config/use-app-form";
import { LoginRequestSchema } from "@/models/Login";
import { useLogin } from "@/services/UserServices";
import { useLocation } from "wouter";
import styles from "./Login.module.css"
import { CiUser, CiLock } from "react-icons/ci";

type LoginProps = {
  rol: string;
  className?: string;
};

export const Login = (props: LoginProps) => {

  const { mutate, error } = useLogin();
  const [, setLocation] = useLocation();

  const formData = useAppForm({
    defaultValues: {
      username: "",
      password: "",
      expectedRole: props.rol,
    },
    validators: {
      onChange: LoginRequestSchema,
    },
    onSubmit: async ({ value }) => {
      const loginData = {
        ...value,
        expectedRole: props.rol
      };
      mutate(loginData);
    },
  });

  return (
    <div className={styles.container} >
      <div className={styles.box}>
        <h2 className={styles.titulo}> 
        Comedor FIUBA
        </h2>
        <formData.AppForm >
          <formData.FormContainer extraError={error ? new Error("Usuario o contraseña incorrectos") : null} submitText="Ingresar"   >
            <formData.AppField name="username" children={(field) => <field.TextField label="Usuario" icono={<CiUser/>} />} />
            <formData.AppField name="password" children={(field) =>
              <field.PasswordField label="Contraseña" 
              icono={ <CiLock/>} />}>
              </formData.AppField>
                
          
          </formData.FormContainer>
        </formData.AppForm>
        
        
        <div>
          <button  className={styles.forgotButton}
            onClick={() => setLocation("/forgot-password")}>
            ¿Olvidaste tu contraseña?
          </button>
        </div>
      </div>
    </div>
  );
};
