import React from "react";
import { Link } from "wouter";

import { ErrorBoundary } from "@/components/ErrorBoundary/ErrorBoundary";
import { useToken, useJwtPayload } from "@/services/TokenContext";
import { MyAcountButton } from "@/components/MyAcountButton/MyAcountButton";
import styles from "./CommonLayout.module.css";

export const CommonLayout = ({ children }: React.PropsWithChildren) => {
  const [tokenState] = useToken();

  return (
    <div className={styles.mainLayout}>
      <ul className={styles.topBar}>{tokenState.state === "LOGGED_OUT" ? <LoggedOutLinks /> : <LoggedInLinks />}</ul>
      <div className={styles.body}>
        <ErrorBoundary>{children}</ErrorBoundary>
      </div>
    </div>
  );
};

const LoggedOutLinks = () => {
  return (
    <>
      <li>
        <Link href="/login">Log in</Link>
      </li>
      <li>
        <Link href="/signup">Sign Up</Link>
      </li>
    </>
  );
};

const LoggedInLinks = () => {
  const [, setTokenState] = useToken();
  const jwtPayload = useJwtPayload();

  const isAdmin = jwtPayload?.role === "ADMIN";
  const isStudent = jwtPayload?.role === "USER";
  const logOut = () => {
    setTokenState({ state: "LOGGED_OUT" });
  };

  return (
    <>
      {isAdmin && (
        <li>
          <Link href="/admin">Administración</Link>
        </li>
      )}
      {isStudent && (
        <li>
          <Link href="/estudiante/mis-pedidos">Mis Pedidos</Link>
        </li>
      )}
      <li>
        <MyAcountButton user={jwtPayload} onLogout={logOut} />
      </li>
    </>
  );
};

