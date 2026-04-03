import { useState } from "react";
import { CommonLayout } from "@/components/CommonLayout/CommonLayout";
import { useAppForm } from "@/config/use-app-form";
import { SignupRequestSchema } from "@/models/Login";
import { useSignup } from "@/services/UserServices";
import styles from "./SignUp.module.css";


export const SignUp = () => {
    const [registrationSuccess, setRegistrationSuccess] = useState(false);
    const { mutate, error } = useSignup();

  const formData = useAppForm({
      defaultValues: {
        username: "",
        password: "",
        name: "",
        surname: "",
        email: "",
        age: "" as any,
        gender: "",
        address: "",
        role: "USER",
      },
      validators: {
        onSubmit: SignupRequestSchema,
      },
      onSubmit: async ({ value }) => {
        const backendData = {
          username: value.username,
          password: value.password,
          nombre: value.name,
          apellido: value.surname,
          email: value.email,
          edad: value.age,
          genero: value.gender,
          domicilio: value.address,
          role: value.role,
        };
        
        mutate(backendData, {
          onSuccess: (data) => {
            console.log("Registration successful:", data);
            setRegistrationSuccess(true);
          },
          onError: (error) => {
            console.error("Registration failed:", error);
            setRegistrationSuccess(false);
          }
        });
      },
    });

  if (registrationSuccess) {
      return (
        <CommonLayout>
          <div style={{ textAlign: 'center', padding: '20px' }}>
            <h1>¡Registro Exitoso! 📧</h1>
            <p>Tu cuenta ha sido creada exitosamente.</p>
            <p><strong>Revisa tu email para verificar tu cuenta antes de iniciar sesión.</strong></p>
            <p>Si no encuentras el email, revisa tu carpeta de spam.</p>
            <button 
              onClick={() => setRegistrationSuccess(false)}
              style={{ marginTop: '10px', padding: '10px 20px' }}
            >
              Registrar otro usuario
            </button>
          </div>
        </CommonLayout>
      );
    }

    return(
        <div className={styles.container}>
            <div className={styles.box}>
            <h2>Completa con tus datos para registrarte</h2>
                <formData.AppForm >
                    <formData.FormContainer extraError={error} submitText="Registrarse" formStyle={styles.formStyle}>
                    <formData.AppField name="username" children={(field) => <field.TextField label="Username" />} />
                    <formData.AppField name="password" children={(field) => <field.PasswordField label="Password" />} />
                    <formData.AppField name="name" children={(field) => <field.TextField label="Nombre" />} />
                    <formData.AppField name="surname" children={(field) => <field.TextField label="Apellido" />} />
                    <formData.AppField name="email" children={(field) => <field.TextField label="Email" />} />
                    <formData.AppField name="age" children={(field) => <field.NumberField label="Edad" />} />
                    <formData.AppField name="gender" children={(field) => <field.SelectField label="Género" options={[{ label: "Masculino", value: "male" }, { label: "Femenino", value: "female" }, { label: "Otro", value: "other" }]} />} />
                    <formData.AppField name="address" children={(field) => <field.TextField label="Domicilio" />} />
                    </formData.FormContainer>
                </formData.AppForm>
            </div>
        </div>
    )
};
