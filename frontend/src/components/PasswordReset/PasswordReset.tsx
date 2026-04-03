import { useEffect, useState, FormEvent } from "react";
import { useLocation } from "wouter";
import styles from "./PasswordReset.module.css";
import { buildApiUrl } from "@/config/env-config";


export default function PasswordReset() {
  const [, setLocation] = useLocation();
  const [token, setToken] = useState("");
  const [password, setPassword] = useState("");
  const [confirm, setConfirm] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    setToken(params.get("token") ?? "");
  }, []);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    if (!token) {
      setError("Token inválido. Solicita un nuevo enlace.");
      return;
    }
    if (password.length < 8) {
      setError("La contraseña debe tener al menos 8 caracteres.");
      return;
    }
    if (password !== confirm) {
      setError("Las contraseñas no coinciden.");
      return;
    }

    setLoading(true);
    try {
      const url = buildApiUrl("/users/password/reset");

      const res = await fetch(url, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ token, newPassword: password }),
      });

      if (!res.ok) {
        const body = await res.text().catch(() => "");
        throw new Error(body || `Error ${res.status}`);
      }

      const responseText = await res.text().catch(
        () => "Contraseña actualizada correctamente"
      );
      setSuccess(responseText);

      setPassword("");
      setConfirm("");
    } catch (err: any) {
      setError(err?.message ?? "Error inesperado al cambiar la contraseña");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className={styles["pr-root"]}>
      <div className={styles["pr-card"]} role="main">
        <h2 className={styles["pr-title"]}>Restablecer contraseña</h2>
        <p className={styles["pr-sub"]}>Introduce tu nueva contraseña — el token se toma de la URL.</p>

        <form onSubmit={handleSubmit} className={styles["pr-form"]} noValidate>
          <label className={styles["pr-label"]}>
            Nueva contraseña
            <input
              className={styles["pr-input"]}
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Al menos 8 caracteres"
              minLength={8}
              required
              autoComplete="new-password"
              disabled={!!success}
            />
          </label>

          <label className={styles["pr-label"]}>
            Confirmar contraseña
            <input
              className={styles["pr-input"]}
              type="password"
              value={confirm}
              onChange={(e) => setConfirm(e.target.value)}
              placeholder="Repite la contraseña"
              required
              autoComplete="new-password"
              disabled={!!success}
            />
          </label>

          {!success && (
            <button className={styles["pr-button"]} type="submit" disabled={loading}>
              {loading ? "Enviando..." : "Restablecer contraseña"}
            </button>
          )}

          {success && (
            <button 
              className={styles["pr-button"]} 
              type="button"
              onClick={() => setLocation("/login")}
            >
              Ir al inicio de sesión
            </button>
          )}
        </form>

        {error && <div className={styles["pr-error"]} role="alert">{error}</div>}
        {success && <div className={styles["pr-success"]} role="status">{success}</div>}
        {!token && <div className={styles["pr-note"]}>No se encontró token en la URL. Solicita un nuevo enlace.</div>}
      </div>
    </div>
  );
}