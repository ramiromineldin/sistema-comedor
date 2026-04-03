import { useMutation } from "@tanstack/react-query";

import { BASE_API_URL } from "@/config/app-query-client";
import { AuthResponseSchema, LoginRequest } from "@/models/Login";
import { useToken } from "@/services/TokenContext";

export function useLogin() {
  const [, setToken] = useToken();

  return useMutation({
    mutationFn: async (req: LoginRequest) => {
      const tokens = await auth("POST", "/auth/login", req);
      setToken({ state: "LOGGED_IN", tokens });
    },
  });
}

export function useRefresh() {
  const [tokenState, setToken] = useToken();

  return useMutation({
    mutationFn: async () => {
      if (tokenState.state !== "LOGGED_IN") {
        return;
      }

      try {
        const refreshToken = tokenState.tokens.refreshToken;
        const tokenPromise = auth("PUT", "/sessions", { refreshToken });
        setToken({ state: "REFRESHING", tokenPromise });
        setToken({ state: "LOGGED_IN", tokens: await tokenPromise });
      } catch (err) {
        setToken({ state: "LOGGED_OUT" });
        throw err;
      }
    },
  });
}

type SignupBackendRequest = {
  username: string;
  password: string;
  nombre: string;
  apellido: string;
  email: string;
  edad: number;
  genero: string;
  domicilio: string;
  role: string;
};

export function useSignup() {

  return useMutation({
    mutationFn: async (req: SignupBackendRequest) => {
      const response = await fetch(BASE_API_URL + "/users/signup", {
        method: "POST",
        headers: {
          Accept: "application/json",
          "Content-Type": "application/json",
        },
        body: JSON.stringify(req),
      });

      if (response.ok) {
        return await response.text();
      } else {
        throw new Error(`Ocurrió un error: ${await response.text()}`);
      }
    },
  });
}

async function auth(method: "PUT" | "POST", endpoint: string, data: object) {
  const response = await fetch(BASE_API_URL + endpoint, {
    method,
    headers: {
      Accept: "application/json",
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  });

  if (response.ok) {
    return AuthResponseSchema.parse(await response.json());
  } else {
    throw new Error(`Failed with status ${response.status}: ${await response.text()}`);
  }
}

export function useEmailVerification() {
  return useMutation({
    mutationFn: async (token: string) => {
      const response = await fetch(BASE_API_URL + `/users/verify?token=${token}`, {
        method: "GET",
        headers: {
          Accept: "application/json",
        },
      });

      if (response.ok) {
        return await response.text();
      } else {
        const errorText = await response.text();
        throw new Error(`Verification failed: ${errorText}`);
      }
    },
  });
}