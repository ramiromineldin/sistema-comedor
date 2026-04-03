import { Redirect, Route, Switch } from "wouter";

import { LoginPersonalScreen } from "@/screens/login/LoginPersonalScreen";
import { MenuDelDiaScreen } from "@/screens/cocina/MenuDelDiaScreen";
import { ControlStockScreen } from "@/screens/cocina/ControlStockScreen";
import PasswordReset from "@/components/PasswordReset/PasswordReset";
import ForgotPassword from "@/components/ForgotPassword/ForgotPassword";
import { useToken, useUserRole } from "@/services/TokenContext";
import InicioScreen from "./screens/inicio/InicioScreen";
import { LoginAdminScreen } from "./screens/login/LoginAdminScreen";
import { LoginEstudianteScreen } from "./screens/login/LoginEstudianteScreen";
import { PedidosScreen } from "./screens/cocina/PersonalScreen";
import { HistorialScreen } from "./screens/cocina/HistorialScreen";

import { StudentHomeScreen } from "./screens/student/StudentHomeScreen";

import { DiscountScreen } from "./screens/student/Discounts/DiscountsScreen";

import { StudentMenuScreen } from "./screens/student/StudentsMenuScreen";
import { StudentOrderScreen } from "./screens/student/StudentsOrdersScreen";
import { AdminIngredientsScreen } from "./screens/admin/ingredientes/AdminIngredientesScreen";
import { AdminCombosScreen } from "./screens/admin/combos/AdminCombosScreen";
import { AdminProductsScreen } from "./screens/admin/productos/AdminProductsScreen";
import { AdminUsersScreen } from "./screens/admin/personal/AdminUsersScreen";
import { SignupScreen } from "./screens/SignUp/SignupScreen";
import { AdminPromotionsScreen } from "./screens/admin/promociones/AdminPromotionsScreen";
import { AdminHomeScreen } from "./screens/admin/AdminHomeScreen";



import VerifyToken from "@/components/VerifyToken/VerifyToken";

export const Navigation = () => {
  const [tokenState] = useToken();
  const userRole = useUserRole();

  switch (tokenState.state) {
    case "LOGGED_IN":
    case "REFRESHING":
      if (userRole === "ADMIN") {
        return (
          <Switch>
            <Route path="/admin" component={AdminHomeScreen} />
            <Route path="/admin/ingredientes" component={AdminIngredientsScreen} />
            <Route path="/admin/productos" component={AdminProductsScreen} />
            <Route path="/admin/combos" component={AdminCombosScreen} />
            <Route path="/admin/promociones" component={AdminPromotionsScreen} />
            <Route path="/admin/personal-cocina" component={AdminUsersScreen} />
            <Route path="/">
              <Redirect href="/admin" />
            </Route>
            <Route>
              <Redirect href="/admin" />
            </Route>
          </Switch>
        );
      }

      if (userRole === "PERSONAL_COCINA") {
        return (
          <Switch>
            {/* Pantalla principal */}
            <Route path="/cocina">
              <PedidosScreen/>
            </Route>

            <Route path="/cocina/menu-del-dia">
              <MenuDelDiaScreen />
            </Route>

            <Route path="/cocina/control-stock">
              <ControlStockScreen />
            </Route>

            <Route path="/cocina/historial">
              <HistorialScreen />
            </Route>

            {/* Fallback general */}
            <Route>
              <Redirect href="/cocina"></Redirect>
            </Route>
          </Switch>
        );


      }

      if (userRole === "USER") {
        return (
          <Switch>
            <Route path="/estudiante/menu-del-dia">
              <StudentMenuScreen />
            </Route>
            <Route path="/estudiante/mis-pedidos">
              <StudentOrderScreen />
            </Route>
            <Route path="/estudiante/combos-especiales">
              <DiscountScreen />
            </Route>
            <Route path="/estudiante">
              <StudentHomeScreen />
            </Route>
            <Route path="/">
              <Redirect href="/estudiante" />
            </Route>
            <Route>
              <Redirect href="/estudiante" />
            </Route>
          </Switch>
        );
      }

      // fallback
      return (
        <Switch>
          <Route>
            <Redirect href="/inicio" />
          </Route>
        </Switch>
      );

    case "LOGGED_OUT":
      return (
        <Switch>
          <Route path="/inicio" >
            <InicioScreen/>
          </Route>
          <Route path="/users/verify">
            <VerifyToken />
          </Route>
          <Route path="/verify"><VerifyToken /></Route>

          <Route path="/users/password/reset">
            <PasswordReset />
          </Route>
          <Route path="/password/reset">
            <PasswordReset />
          </Route>
          <Route path="/password-reset">
            <PasswordReset />
          </Route>

          <Route path="/forgot-password">
            <ForgotPassword />
          </Route>

          <Route path="/login-estudiante">
            <LoginEstudianteScreen />
          </Route>
          <Route path="/signup">
            <SignupScreen />
          </Route>
          <Route path="/login-personal">
            <LoginPersonalScreen />
          </Route>
          <Route path="/login-admin">
            <LoginAdminScreen />
          </Route>
          <Route>
            <Redirect href="/inicio" />
          </Route>
        </Switch>
      );

    default:
      return tokenState satisfies never;
  }
};
