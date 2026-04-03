import { useId, useState } from "react";

import { ErrorContainer } from "@/components/form-components/ErrorContainer/ErrorContainer";
import { useFieldContext , useFormContext} from "@/config/form-context";
import { IoMdEye } from "react-icons/io";
import { IoEyeOffSharp } from "react-icons/io5";

import styles from "./InputFields.module.css";

export const TextField = ({ label , icono }: { label: string; icono?:React.ReactNode; }) => {
  return <FieldWithType type="text" label={label} icono= {icono}/>;
};

export const PasswordField = ({ label, icono }: { label: string; icono?: React.ReactNode; }) => {
  return <FieldWithType type="password" label={label} icono={icono}/>;
};

export const NumberField = ({ label }: { label: string }) => {
  return <FieldWithType type="number" label={label} />;
}

export const SelectField = ({ label, options }: { label: string; options: { label: string; value: string }[] }) => {
  return <FieldWithType type="select" label={label} options={options} />;
}

const FieldWithType = ({ label, type, icono,  options }: { label: string; type: string; icono?: React.ReactNode ; options?: { label: string; value: string }[] }) => {
  const id = useId();
  const field = useFieldContext<any>(); 
  const form = useFormContext();
  const showErrors = form.state.submissionAttempts > 0;
  const [mostrar,setMostrar] = useState(false);
  
  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    if (type === "number") {
      const numValue = parseInt(e.target.value) || 0;
      field.handleChange(numValue);
    } else if (type === "select") {
      const selectedOption = options?.find((option) => option.value === e.target.value || option.label === e.target.value);
      if (selectedOption) {
        field.handleChange(selectedOption.value);
      } else {
        field.handleChange("");
      }
    } else {
      field.handleChange(e.target.value);
    }
  };



  if (type === "select") {
    return (
      <>
        <label htmlFor={id} className={styles.label}>
          {label}
        </label>
        <div className={styles.dataContainer}>
          <select
            id={id}
            name={field.name}
            value={field.state.value}
            className={styles.input}
            onBlur={field.handleBlur}
            onChange={handleChange}
          >
            <option value="">Selecciona una opción</option>
            {options?.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
          <ErrorContainer errors={ showErrors ? field.state.meta.errors : []} />
        </div>
      </>
    );
  }

  return (
    <>
      <div className={styles.dataContainer}>
        <div className={styles.inputWrapper}>
          <span className={styles.icono}>
            {icono}
          </span>
          <input
            id={id}
            name={field.name}
            value={field.state.value}
            className={styles.input}
            type={type === "password" ? (mostrar ? "text" : "password") : type}
            onBlur={field.handleBlur}
            onChange={handleChange}
            {...(type === "number" ? { min: 1, max: 120 } : {})}
            
            placeholder={label}
          />

          {type === "password" && (
            <button type="button" className={styles.showPassword} onClick={()=> setMostrar(!mostrar)}>
              {mostrar ? <IoMdEye/> : <IoEyeOffSharp/> }
            </button>
        
          )}
        </div>
        <ErrorContainer errors={showErrors ? field.state.meta.errors : []} />
      </div>
    </>
  );
};
