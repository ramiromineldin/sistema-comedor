import { useState, FormEvent } from "react";
import { useLocation } from "wouter";
import styles from "./ForgotPassword.module.css";
import { buildApiUrl } from "@/config/env-config";


export default function ForgotPassword() {
  const [, setLocation] = useLocation();
  const [email, setEmail] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

async function handleSubmit(e: FormEvent) {
  e.preventDefault();
  setError(null);
  setSuccess(null);

  if (!email.trim()) {
    setError("Por favor ingresa tu email.");
    return;
  }

  if (!isValidEmail(email)) {
    setError("Por favor ingresa un email válido.");
    return;
  }

  setLoading(true);
  try {
    const url = buildApiUrl("/users/password/forgot");

    const res = await fetch(url, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email: email.trim() }),
    });

    if (!res.ok) {
      const body = await res.json().catch(() => ({}));
      throw new Error(body?.message ?? `Error ${res.status}`);
    }

    setSuccess(
      "Si el email existe en nuestro sistema, recibirás un enlace para restablecer tu contraseña. Revisa tu bandeja de entrada."
    );

    setTimeout(() => {
      setLocation("/login");
    }, 5000);
  } catch (err: any) {
    setError(err?.message ?? "Error inesperado. Inténtalo de nuevo.");
  } finally {
    setLoading(false);
  }
}

  function isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  return (
    <div className={styles.root}>
      <div className={styles.card} role="main">
        <h2 className={styles.title}>¿Olvidaste tu contraseña?</h2>
        <p className={styles.subtitle}>
          Ingresa tu email y te enviaremos un enlace para restablecer tu contraseña.
        </p>

        <form onSubmit={handleSubmit} className={styles.form} noValidate>
          <label className={styles.label}>
            Email
            <input
              className={styles.input}
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="tu@email.com"
              required
              autoComplete="email"
            />
          </label>

          <button 
            className={styles.submitButton} 
            type="submit" 
            disabled={loading}
          >
            {loading ? "Enviando..." : "Enviar enlace de restablecimiento"}
          </button>
        </form>

        {error && (
          <div className={styles.error} role="alert">
            {error}
          </div>
        )}
        
        {success && (
          <div className={styles.success} role="status">
            {success}
          </div>
        )}

        <div className={styles.actions}>
          <button
            className={styles.backButton}
            onClick={() => setLocation("/login")}
            type="button"
          >
            Volver al login
          </button>
        </div>
      </div>
    </div>
  );
}