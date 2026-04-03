import React, { Dispatch, useCallback, useContext, useState } from "react";

import { AuthResponse, AuthResponseSchema } from "@/models/Login";
import { useRefresh } from "@/services/UserServices";

const TOKEN_STORAGE_KEY = "tokens";

type TokenContextData =
  | {
      state: "LOGGED_OUT";
    }
  | {
      state: "REFRESHING";
      tokenPromise: Promise<AuthResponse>;
    }
  | {
      state: "LOGGED_IN";
      tokens: AuthResponse;
    };

const TokenContext = React.createContext<[TokenContextData, Dispatch<TokenContextData>] | null>(null);

export const TokenProvider = ({ children }: React.PropsWithChildren) => {
  const [state, setInternalState] = useState<TokenContextData>(getInitialTokenState);
  const setState = useCallback(
    (state: TokenContextData) => {
      if (state.state === "LOGGED_IN") {
        localStorage.setItem(TOKEN_STORAGE_KEY, JSON.stringify(state.tokens));
      } else {
        localStorage.removeItem(TOKEN_STORAGE_KEY);
      }
      setInternalState(state);
    },
    [setInternalState],
  );
  return <TokenContext.Provider value={[state, setState]}>{children}</TokenContext.Provider>;
};

export function useToken() {
  const context = useContext(TokenContext);
  if (context === null) {
    throw new Error("React tree should be wrapped in TokenProvider");
  }
  return context;
}

export function useAccessTokenGetter() {
  const [tokenState] = useToken();

  return async function getAccessToken() {
    switch (tokenState.state) {
      case "LOGGED_OUT":
        throw new Error("Auth needed for service");
      case "REFRESHING":
        return (await tokenState.tokenPromise).accessToken;
      case "LOGGED_IN":
        return tokenState.tokens.accessToken;
      default:
        // Make the compiler check this is unreachable
        return tokenState satisfies never;
    }
  };
}

// Helper function to extract JWT payload
export function useJwtPayload() {
  const [tokenState] = useToken();

  if (tokenState.state !== "LOGGED_IN") {
    return null;
  }

  try {
    const token = tokenState.tokens.accessToken;
    const payload = JSON.parse(atob(token.split('.')[1]));
    return {
      sub: payload.sub as string, // username
      role: payload.role as "ADMIN" | "PERSONAL_COCINA" | "USER",
      iat: payload.iat as number, // issued at
      exp: payload.exp as number, // expiration
    };
  } catch (error) {
    console.error("Error decoding JWT:", error);
    return null;
  }
}

export function useUserRole() {
  const payload = useJwtPayload();
  return payload?.role || null;
}

export function useLogout() {
  const [, setState] = useToken();
  
  return function logout() {
    setState({ state: "LOGGED_OUT" });
  };
}

export function useHandleResponse() {
  const { mutate } = useRefresh();

  return async function handleResponse<T>(response: Response, parse: (json: unknown) => T) {
    if (response.status === 401) {
      mutate();
      throw new Error("Attempting token refresh");
    } else if (response.ok) {
      return parse(await response.json());
    } else {
      throw new Error(`Failed with status ${response.status}: ${await response.text()}`);
    }
  };
}

const getInitialTokenState = (): TokenContextData => {
  const storedData = localStorage.getItem(TOKEN_STORAGE_KEY);
  if (storedData) {
    try {
      const tokens = AuthResponseSchema.parse(JSON.parse(storedData));
      return { state: "LOGGED_IN", tokens };
    } catch (err) {
      console.error(err);
    }
  }
  return { state: "LOGGED_OUT" };
};
