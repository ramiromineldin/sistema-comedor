import { useState, useEffect } from "react";
import { useToken } from "@/services/TokenContext";

interface UseFetchState<T> {
  data: T | null;
  loading: boolean;
  error: string | null;
}

export default function useFetch<T = any>(url: string): UseFetchState<T> {
  const [data, setData] = useState<T | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [tokenState] = useToken();

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        setError(null);
        
        if (tokenState.state !== "LOGGED_IN") {
          setError("Usuario no autenticado");
          setLoading(false);
          return;
        }

        const headers: Record<string, string> = {
          "Content-Type": "application/json",
          "Accept": "application/json"
        };

        if (tokenState.tokens?.accessToken) {
          headers.Authorization = `Bearer ${tokenState.tokens.accessToken}`;
        }
        
        const response = await fetch(url, {
          method: "GET",
          headers
        });
        
        if (!response.ok) {
          throw new Error(`Error ${response.status}: ${response.statusText}`);
        }
        
        const result = await response.json();
        setData(result);
      } catch (err: any) {
        setError(err.message || "Error al cargar los datos");
        console.error("Error en useFetch:", err);
      } finally {
        setLoading(false);
      }
    };

    if (url) {
      fetchData();
    }
  }, [url, tokenState]);

  return { data, loading, error };
}