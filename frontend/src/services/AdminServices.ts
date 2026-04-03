import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { BASE_API_URL } from "@/config/app-query-client";
import { useToken } from "@/services/TokenContext";

export type KitchenStaffUser = {
  id: number;
  username: string;
  nombre: string;
  apellido: string;
  email: string;
  edad: number;
  genero: string;
  domicilio: string;
  role: string;
  emailVerified: boolean;
};

export type KitchenStaffCreateRequest = {
  username: string;
  password: string;
  nombre: string;
  apellido: string;
  email: string;
  edad: number;
  genero: string;
  domicilio: string;
};

export type KitchenStaffUpdateRequest = {
  nombre: string;
  apellido: string;
  email: string;
  edad: number;
  genero: string;
  domicilio: string;
};

export function useKitchenStaffList() {
  const [tokenState] = useToken();

  return useQuery({
    queryKey: ["kitchenStaff"],
    queryFn: async (): Promise<KitchenStaffUser[]> => {
      const token = tokenState.state === "LOGGED_IN" ? tokenState.tokens.accessToken : "";
      const response = await fetch(BASE_API_URL + "/admin/users/kitchen-staff", {
        headers: {
          Authorization: `Bearer ${token}`,
          Accept: "application/json",
        },
      });

      if (response.ok) {
        return await response.json();
      } else {
        throw new Error(`Failed to fetch kitchen staff: ${response.status}`);
      }
    },
    enabled: tokenState.state === "LOGGED_IN",
  });
}

export function useCreateKitchenStaff() {
  const [tokenState] = useToken();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (req: KitchenStaffCreateRequest) => {
      const token = tokenState.state === "LOGGED_IN" ? tokenState.tokens.accessToken : "";
      const response = await fetch(BASE_API_URL + "/admin/users/kitchen-staff", {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`, 
          Accept: "application/json",
          "Content-Type": "application/json",
        },
        body: JSON.stringify(req),
      });

      if (!response.ok) {
        throw new Error(`Error creating kitchen staff: ${await response.text()}`);
      }
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["kitchenStaff"] });
    },
  });
}

export function useUpdateKitchenStaff() {
  const [tokenState] = useToken();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ id, data }: { id: number; data: KitchenStaffUpdateRequest }) => {
      const token = tokenState.state === "LOGGED_IN" ? tokenState.tokens.accessToken : "";
      const response = await fetch(BASE_API_URL + `/admin/users/kitchen-staff/${id}`, {
        method: "PUT",
        headers: {
          Authorization: `Bearer ${token}`, 
          Accept: "application/json",
          "Content-Type": "application/json",
        },
        body: JSON.stringify(data),
      });

      if (!response.ok) {
        throw new Error(`Error updating kitchen staff: ${await response.text()}`);
      }
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["kitchenStaff"] });
    },
  });
}

export function useDeleteKitchenStaff() {
  const [tokenState] = useToken();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (id: number) => {
      const token = tokenState.state === "LOGGED_IN" ? tokenState.tokens.accessToken : "";
      const response = await fetch(BASE_API_URL + `/admin/users/kitchen-staff/${id}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${token}`, 
          Accept: "application/json",
        },
      });

      if (!response.ok) {
        throw new Error(`Error deleting kitchen staff: ${await response.text()}`);
      }
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["kitchenStaff"] });
    },
  });

}

export type Ingredient = {
  id: number;
  name: string;
  description: string;
  price: number;
  stock: number;
  available: boolean;
};

export type IngredientCreateRequest = {
  name: string;
  description: string;
  price: number;
  initialStock: number;
};

export function useIngredientList() {
  const [tokenState] = useToken();

  return useQuery({
    queryKey: ["ingredients"],
    queryFn: async (): Promise<Ingredient[]> => {
      const token =
        tokenState.state === "LOGGED_IN"
          ? tokenState.tokens.accessToken
          : "";
      const response = await fetch(BASE_API_URL + "/ingredients", {
        headers: {
          Authorization: `Bearer ${token}`,
          Accept: "application/json",
        },
      });

      if (!response.ok) {
        throw new Error(`Failed to fetch ingredients: ${response.status}`);
      }
      return await response.json();
    },
    enabled: tokenState.state === "LOGGED_IN",
  });
}

export function useCreateIngredient() {
  const [tokenState] = useToken();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (req: IngredientCreateRequest) => {
      const token =
        tokenState.state === "LOGGED_IN"
          ? tokenState.tokens.accessToken
          : "";
      const response = await fetch(BASE_API_URL + "/ingredients", {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          Accept: "application/json",
          "Content-Type": "application/json",
        },
        body: JSON.stringify(req),
      });

      if (!response.ok) {
        throw new Error(`Error creating ingredient: ${await response.text()}`);
      }
      return await response.json();
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["ingredients"] });
    },
  });
}

export function useIncrementStock() {
  const [tokenState] = useToken();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (id: number) => {
      const token =
        tokenState.state === "LOGGED_IN"
          ? tokenState.tokens.accessToken
          : "";
      const response = await fetch(
        BASE_API_URL + `/ingredients/increment/${id}`,
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`,
            Accept: "application/json",
          },
        }
      );
      if (!response.ok) {
        throw new Error(`Error incrementing stock: ${await response.text()}`);
      }
      return await response.json();
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["ingredients"] });
    },
  });
}

export function useDecrementStock() {
  const [tokenState] = useToken();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (id: number) => {
      const token =
        tokenState.state === "LOGGED_IN"
          ? tokenState.tokens.accessToken
          : "";
      const response = await fetch(
        BASE_API_URL + `/ingredients/decrement/${id}`,
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`,
            Accept: "application/json",
          },
        }
      );
      if (!response.ok) {
        throw new Error(`Error decrementing stock: ${await response.text()}`);
      }
      return await response.json();
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["ingredients"] });
    },
  });
}

// ====================== PRODUCTS ======================

export type Product = {
  id: number;
  name: string;
  description: string;
  price: number;
  available: boolean;
  ingredientes: Ingredient[]; // relación desde backend
};

export type ProductCreateRequest = {
  name: string;
  description: string;
  price: number;
  ingredientesIds: number[]; // IDs de ingredientes seleccionados
};

export function useProductList() {
  const [tokenState] = useToken();

  return useQuery({
    queryKey: ["products"],
    queryFn: async (): Promise<Product[]> => {
      const token =
        tokenState.state === "LOGGED_IN"
          ? tokenState.tokens.accessToken
          : "";
      const response = await fetch(BASE_API_URL + "/products", {
        headers: {
          Authorization: `Bearer ${token}`,
          Accept: "application/json",
        },
      });

      if (!response.ok)
        throw new Error(`Failed to fetch products: ${response.status}`);
      return await response.json();
    },
    enabled: tokenState.state === "LOGGED_IN",
  });
}

export function useCreateProduct() {
  const [tokenState] = useToken();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (req: ProductCreateRequest) => {
      const token =
        tokenState.state === "LOGGED_IN"
          ? tokenState.tokens.accessToken
          : "";
      const response = await fetch(BASE_API_URL + "/products", {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          Accept: "application/json",
          "Content-Type": "application/json",
        },
        body: JSON.stringify(req),
      });
      if (!response.ok)
        throw new Error(`Error creating product: ${await response.text()}`);
      return await response.json();
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["products"] });
    },
  });
}

export function useDeleteProduct() {
  const [tokenState] = useToken();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (id: number) => {
      const token =
        tokenState.state === "LOGGED_IN"
          ? tokenState.tokens.accessToken
          : "";
      const response = await fetch(BASE_API_URL + `/products/${id}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${token}`,
          Accept: "application/json",
        },
      });
      if (!response.ok)
        throw new Error(`Error deleting product: ${await response.text()}`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["products"] });
    },
  });
}

// ====================== COMBOS ======================

export type Combo = {
  id: number;
  name: string;
  description: string;
  discount: number;
  price: number;
  subAlimentos: Array<{
  id: number;
  name: string;
  tipo: string;
  price: number;
  }>;
};

export type ComboCreateRequest = {
  name: string;
  description: string;
  discount: number;
  alimentoIds: number[];
};

// 🔹 Obtener todos los combos
export function useComboList() {
  const [tokenState] = useToken();

  return useQuery({
    queryKey: ["combos"],
    queryFn: async (): Promise<Combo[]> => {
      const token =
        tokenState.state === "LOGGED_IN"
          ? tokenState.tokens.accessToken
          : "";
      const response = await fetch(BASE_API_URL + "/admin/combos", {
        headers: {
          Authorization: `Bearer ${token}`,
          Accept: "application/json",
        },
      });

      if (!response.ok)
        throw new Error(`Failed to fetch combos: ${response.status}`);
      return await response.json();
    },
    enabled: tokenState.state === "LOGGED_IN",
  });
}

// 🔹 Crear combo
export function useCreateCombo() {
  const [tokenState] = useToken();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (req: ComboCreateRequest) => {
      const token =
        tokenState.state === "LOGGED_IN"
          ? tokenState.tokens.accessToken
          : "";
      const response = await fetch(BASE_API_URL + "/admin/combos", {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          Accept: "application/json",
          "Content-Type": "application/json",
        },
        body: JSON.stringify(req),
      });
      if (!response.ok)
        throw new Error(`Error creating combo: ${await response.text()}`);
      return await response.json();
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["combos"] });
    },
  });
}

// 🔹 Eliminar combo
export function useDeleteCombo() {
  const [tokenState] = useToken();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (id: number) => {
      const token =
        tokenState.state === "LOGGED_IN"
          ? tokenState.tokens.accessToken
          : "";
      const response = await fetch(BASE_API_URL + `/admin/combos/${id}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${token}`,
          Accept: "application/json",
        },
      });
      if (!response.ok)
        throw new Error(`Error deleting combo: ${await response.text()}`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["combos"] });
    },
  });
}

// 🔹 Actualizar descuento de combo
export function useUpdateComboDiscount() {
    const [tokenState] = useToken();
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: async ({ id, discount }: { id: number; discount: number }) => {
            const token =
                tokenState.state === "LOGGED_IN"
                    ? tokenState.tokens.accessToken
                    : "";
            const decimalDiscount = discount / 100;
            const response = await fetch(
                BASE_API_URL + `/admin/combos/${id}/descuento?discount=${decimalDiscount}`,
                {
                    method: "PATCH",
                    headers: {
                        Authorization: `Bearer ${token}`,
                        Accept: "application/json",
                    },
                }
            );
            if (!response.ok)
                throw new Error(`Error updating discount: ${await response.text()}`);
            return await response.json();
        },
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["combos"] });
        },
    });
}

// ==========================================
// 🎉 PROMOCIONES
// ==========================================

export type PromotionItem = {
  id: number;
  productId: number;
  productName: string;
  originalPrice: number;
};

export type PromotionType = 
  | "PERCENTAGE_DISCOUNT" 
  | "RECURRING_DAY_DISCOUNT"
  | "AMOUNT_DISCOUNT" 
  | "BUY_X_PAY_Y"
  | "TIME_RESTRICTED_BUY_X_PAY_Y";

export type DayOfWeek = 
  | "MONDAY" 
  | "TUESDAY" 
  | "WEDNESDAY" 
  | "THURSDAY" 
  | "FRIDAY" 
  | "SATURDAY" 
  | "SUNDAY";

export type PromotionResponse = {
  id: number;
  name: string;
  description: string;
  startDate: string;
  endDate: string;
  active: boolean;
  type: PromotionType;
  originalPrice: number;
  discountPercentage?: number;
  discountAmount?: number;
  minimumPurchaseAmount?: number;
  buyQuantity?: number;
  payQuantity?: number;
  recurringDay?: DayOfWeek;
  startTime?: string;
  endTime?: string;
  items: PromotionItem[];
};

export type PromotionItemCreateRequest = {
  productId: number;
};

export type PromotionCreateRequest = {
  name: string;
  description: string;
  startDate: string;
  endDate: string;
  type: PromotionType;
  discountPercentage?: number;
  discountAmount?: number;
  minimumPurchaseAmount?: number;
  buyQuantity?: number;
  payQuantity?: number;
  recurringDay?: DayOfWeek;
  startTime?: string;
  endTime?: string;
  items: PromotionItemCreateRequest[];
};

// 🔹 Listar promociones
export function usePromotionList() {
  const [tokenState] = useToken();

  return useQuery({
    queryKey: ["promotions"],
    queryFn: async (): Promise<PromotionResponse[]> => {
      const token =
        tokenState.state === "LOGGED_IN" ? tokenState.tokens.accessToken : "";
      const response = await fetch(BASE_API_URL + "/admin/promotions", {
        headers: {
          Authorization: `Bearer ${token}`,
          Accept: "application/json",
        },
      });

      if (response.ok) {
        return await response.json();
      } else {
        throw new Error(`Failed to fetch promotions: ${response.status}`);
      }
    },
    enabled: tokenState.state === "LOGGED_IN",
  });
}

// 🔹 Crear promoción
export function useCreatePromotion() {
  const [tokenState] = useToken();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (req: PromotionCreateRequest) => {
      const token =
        tokenState.state === "LOGGED_IN" ? tokenState.tokens.accessToken : "";
      
      // Convertir las fechas a formato ISO completo
      const startDateTime = new Date(req.startDate).toISOString();
      const endDateTime = new Date(req.endDate + "T23:59:59").toISOString();

      const response = await fetch(BASE_API_URL + "/admin/promotions", {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          Accept: "application/json",
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          ...req,
          startDate: startDateTime,
          endDate: endDateTime,
        }),
      });

      if (!response.ok) {
        throw new Error(`Error creating promotion: ${await response.text()}`);
      }
      return await response.json();
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["promotions"] });
    },
  });
}

// 🔹 Eliminar promoción
export function useDeletePromotion() {
  const [tokenState] = useToken();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (id: number) => {
      const token =
        tokenState.state === "LOGGED_IN" ? tokenState.tokens.accessToken : "";
      const response = await fetch(BASE_API_URL + `/admin/promotions/${id}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${token}`,
          Accept: "application/json",
        },
      });
      if (!response.ok)
        throw new Error(`Error deleting promotion: ${await response.text()}`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["promotions"] });
    },
  });
}

// 🔹 Activar promoción
export function useActivatePromotion() {
  const [tokenState] = useToken();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (id: number) => {
      const token =
        tokenState.state === "LOGGED_IN" ? tokenState.tokens.accessToken : "";
      const response = await fetch(
        BASE_API_URL + `/admin/promotions/${id}/activate`,
        {
          method: "PATCH",
          headers: {
            Authorization: `Bearer ${token}`,
            Accept: "application/json",
          },
        }
      );
      if (!response.ok)
        throw new Error(`Error activating promotion: ${await response.text()}`);
      return await response.json();
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["promotions"] });
    },
  });
}

// 🔹 Desactivar promoción
export function useDeactivatePromotion() {
  const [tokenState] = useToken();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (id: number) => {
      const token =
        tokenState.state === "LOGGED_IN" ? tokenState.tokens.accessToken : "";
      const response = await fetch(
        BASE_API_URL + `/admin/promotions/${id}/desactivate`,
        {
          method: "PATCH",
          headers: {
            Authorization: `Bearer ${token}`,
            Accept: "application/json",
          },
        }
      );
      if (!response.ok)
        throw new Error(
          `Error deactivating promotion: ${await response.text()}`
        );
      return await response.json();
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["promotions"] });
    },
  });
}

