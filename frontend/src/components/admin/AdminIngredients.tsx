import { useState } from "react";
import {
  useIngredientList,
  useCreateIngredient,
  useIncrementStock,
  useDecrementStock,
} from "@/services/AdminServices";
import type { IngredientCreateRequest } from "@/services/AdminServices";

export const AdminIngredients = () => {
  const [showForm, setShowForm] = useState(false);

  const { data: ingredientes, isLoading, error } = useIngredientList();
  const createMutation = useCreateIngredient();
  const incMutation = useIncrementStock();
  const decMutation = useDecrementStock();

  const handleCreate = (data: IngredientCreateRequest) => {
    createMutation.mutate(data, {
      onSuccess: () => setShowForm(false),
    });
  };

  if (isLoading)
    return (
        <div>Cargando ingredientes...</div>
     
    );

  if (error)
    return (
     
        <div style={{ color: "red" }}>Error: {error.message}</div>
     
    );


    return (
    
      <div style={{ padding: "20px" }}>
        <h1>Gestión de Ingredientes</h1>

        <button
          onClick={() => setShowForm(true)}
          style={{
            marginBottom: "20px",
            padding: "10px 20px",
            backgroundColor: "#007bff",
            color: "white",
            border: "none",
            borderRadius: "4px",
          }}
        >
          Nuevo Ingrediente
        </button>

        {showForm && (
          <IngredientForm
            onSubmit={handleCreate}
            onCancel={() => setShowForm(false)}
            isSubmitting={createMutation.isPending}
            error={createMutation.error}
          />
        )}

        <h2>Lista de Ingredientes</h2>
        <table
  style={{
    width: "100%",
    borderCollapse: "collapse",
    marginTop: "10px",
    backgroundColor: "#ffffff",
    borderRadius: "8px",
    overflow: "hidden",
    boxShadow: "0 2px 5px rgba(0,0,0,0.1)",
  }}
>
  <thead>
    <tr style={{ backgroundColor: "#2c3e50", color: "white" }}>
      <th style={{ padding: "12px", border: "1px solid #ddd", textAlign: "left" }}>Nombre</th>
      <th style={{ padding: "12px", border: "1px solid #ddd", textAlign: "left" }}>Descripción</th>
      <th style={{ padding: "12px", border: "1px solid #ddd", textAlign: "center" }}>Precio</th>
      <th style={{ padding: "12px", border: "1px solid #ddd", textAlign: "center" }}>Stock</th>
      <th style={{ padding: "12px", border: "1px solid #ddd", textAlign: "center" }}>Acciones</th>
    </tr>
  </thead>

  <tbody>
    {ingredientes?.map((ing, index) => (
      <tr
        key={ing.id}
        style={{
          backgroundColor: index % 2 === 0 ? "#f9f9f9" : "#ffffff",
          color: "#333",
        }}
      >
        <td style={{ padding: "10px", border: "1px solid #ddd" }}>{ing.name}</td>
        <td style={{ padding: "10px", border: "1px solid #ddd" }}>{ing.description}</td>
        <td style={{ padding: "10px", border: "1px solid #ddd", textAlign: "center" }}>
          ${ing.price}
        </td>
        <td style={{ padding: "10px", border: "1px solid #ddd", textAlign: "center" }}>
          {ing.stock}
        </td>
        <td style={{ padding: "10px", border: "1px solid #ddd", textAlign: "center" }}>
          <button
            onClick={() => incMutation.mutate(ing.id)}
            style={{
              backgroundColor: "#28a745",
              color: "white",
              border: "none",
              borderRadius: "3px",
              padding: "5px 10px",
              marginRight: "5px",
              cursor: "pointer",
            }}
          >
            +1
          </button>
          <button
            onClick={() => decMutation.mutate(ing.id)}
            style={{
              backgroundColor: "#dc3545",
              color: "white",
              border: "none",
              borderRadius: "3px",
              padding: "5px 10px",
              cursor: "pointer",
            }}
          >
            -1
          </button>
        </td>
      </tr>
    ))}
  </tbody>
</table>

      </div>
    
  );
};

// ==== estilos ====
export const th = { border: "1px solid #ddd", padding: "10px" };
export const td = { border: "1px solid #ddd", padding: "10px", textAlign: "center" };
const btnGreen = {
  backgroundColor: "#28a745",
  color: "white",
  border: "none",
  borderRadius: "3px",
  padding: "5px 10px",
  marginRight: "5px",
};
const btnRed = {
  backgroundColor: "#dc3545",
  color: "white",
  border: "none",
  borderRadius: "3px",
  padding: "5px 10px",
};

// ==== formulario ====
const IngredientForm = ({
  onSubmit,
  onCancel,
  isSubmitting,
  error,
}: {
  onSubmit: (data: { name: string; description: string; price: number; initialStock: number }) => void;
  onCancel: () => void;
  isSubmitting: boolean;
  error: Error | null;
}) => {
  const [formData, setFormData] = useState({
    name: "",
    description: "",
    price: 0,
    initialStock: 0,
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit(formData);
  };

  return (
    <form
      onSubmit={handleSubmit}
      style={{
        border: "1px solid #ddd",
        padding: "20px",
        borderRadius: "4px",
        backgroundColor: "#2E3C50",
        marginBottom: "20px",
        maxWidth: "400px",
      }}
    >
      <h3>Crear nuevo ingrediente</h3>
      {error && <p style={{ color: "red" }}>Error: {error.message}</p>}

      <input
        type="text"
        placeholder="Nombre"
        value={formData.name}
        onChange={(e) => setFormData({ ...formData, name: e.target.value })}
        required
        style={input}
      />
      <input
        type="text"
        placeholder="Descripción"
        value={formData.description}
        onChange={(e) =>
          setFormData({ ...formData, description: e.target.value })
        }
        required
        style={input}
      />
      <input
        type="number"
        placeholder="Precio"
        value={formData.price}
        onChange={(e) =>
          setFormData({ ...formData, price: parseFloat(e.target.value) })
        }
        required
        min="0"
        style={input}
      />
      <input
        type="number"
        placeholder="Stock inicial"
        value={formData.initialStock}
        onChange={(e) =>
          setFormData({ ...formData, initialStock: parseInt(e.target.value) })
        }
        required
        min="0"
        style={input}
      />

      <div style={{ display: "flex", gap: "10px", marginTop: "10px" }}>
        <button
          type="submit"
          disabled={isSubmitting}
          style={{
            ...btnGreen,
            cursor: isSubmitting ? "not-allowed" : "pointer",
          }}
        >
          {isSubmitting ? "Guardando..." : "Crear"}
        </button>
        <button type="button" onClick={onCancel} style={btnRed}>
          Cancelar
        </button>
      </div>
    </form>
  );
};

const input = {
  display: "block",
  marginBottom: "10px",
  padding: "8px",
  width: "100%",
  border: "1px solid #ccc",
  borderRadius: "4px",
};
