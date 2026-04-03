import { useEffect, useState } from "react";
import { useLocation } from "wouter";
import styles from "./VerifyToken.module.css";
import { BASE_API_URL } from "@/config/app-query-client";

export default function VerifyToken() {
  const [, setLocation] = useLocation();
  const [, setToken] = useState("");
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const t = params.get("token") ?? "";
    setToken(t);

    const verify = async () => {
      if (!t) {
        setError("Token no provisto en la URL.");
        setLoading(false);
        return;
      }

      try {
        const apiBase = BASE_API_URL;
        const url = `${apiBase.replace(/\/$/, "")}/users/verify?token=${encodeURIComponent(t)}`;
        const res = await fetch(url, { method: "GET" });

        const text = await res.text().catch(() => "");
        if (res.ok) {
          setMessage(text || "Verificación exitosa.");
        } else {
          setError(text || `Error ${res.status}`);
        }
      } catch (e: any) {
        setError(e?.message ?? "Error de conexión.");
      } finally {
        setLoading(false);
      }
    };

    verify();
  }, []);

  return (
    <div className={styles.root}>
      <div className={styles.card} role="main">
        <h2 className={styles.title}>Verificación de token</h2>

        {loading && <div className={styles.info}>Verificando...</div>}

        {!loading && message && (
          <>
            <div className={styles.success} role="status">
              {message}
            </div>
            <div className={styles.actions}>
              <button className={styles.primaryButton} onClick={() => setLocation("/login")}>
                Ir al login
              </button>
            </div>
          </>
        )}

        {!loading && error && (
          <>
            <div className={styles.error} role="alert">
              {error}
            </div>
            <div className={styles.actions}>
              <button className={styles.primaryButton} onClick={() => setLocation("/login")}>
                Ir al login
              </button>
            </div>
          </>
        )}
      </div>
    </div>
  );
}