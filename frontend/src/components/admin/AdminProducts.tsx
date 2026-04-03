import { useState } from "react";
import {
  useProductList,
  useCreateProduct,
  useDeleteProduct,
  useIngredientList,
} from "@/services/AdminServices";
import type {
  ProductCreateRequest,
  Ingredient,
} from "@/services/AdminServices";

export const AdminProducts = () => {
  const [showForm, setShowForm] = useState(false);
  const { data: productos, isLoading, error } = useProductList();
  const { data: ingredientes } = useIngredientList();
  const createMutation = useCreateProduct();
  const deleteMutation = useDeleteProduct();

  const handleCreate = (data: ProductCreateRequest) => {
    createMutation.mutate(data, {
      onSuccess: () => setShowForm(false),
    });
  };

  const handleDelete = (id: number) => {
    if (confirm("¿Seguro que querés eliminar este producto?")) {
      deleteMutation.mutate(id);
    }
  };

  if (isLoading)
    return (
      
        <div>Cargando productos...</div>
     
    );
  if (error)
    return (
      
        <div style={{ color: "red" }}>Error: {error.message}</div>
     
    );

  return (
    
      <div style={{ padding: "20px" }}>
        <h1>Gestión de Productos</h1>

        <button
          onClick={() => setShowForm(true)}
          style={btnBlue}
        >
          Nuevo Producto
        </button>

        {showForm && (
          <ProductForm
            onSubmit={handleCreate}
            onCancel={() => setShowForm(false)}
            isSubmitting={createMutation.isPending}
            error={createMutation.error}
            ingredientes={ingredientes || []}
          />
        )}

        <h2>Lista de Productos</h2>
        {productos && productos.length > 0 ? (
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
              <th style={{ padding: "12px", border: "1px solid #ddd", textAlign: "center" }}>Disponible</th>
              {/* <th style={{ padding: "12px", border: "1px solid #ddd", textAlign: "center" }}>Ingredientes</th> */}
              <th style={{ padding: "12px", border: "1px solid #ddd", textAlign: "center" }}>Acciones</th>
            </tr>
          </thead>
        
          <tbody>
            {productos.map((p, index) => (
              <tr
                key={p.id}
                style={{
                  backgroundColor: index % 2 === 0 ? "#f9f9f9" : "#ffffff",
                  color: "#333",
                }}
              >
                <td style={{ padding: "10px", border: "1px solid #ddd" }}>{p.name}</td>
                <td style={{ padding: "10px", border: "1px solid #ddd" }}>{p.description}</td>
                <td style={{ padding: "10px", border: "1px solid #ddd", textAlign: "center" }}>
                  ${p.price}
                </td>
                <td style={{ padding: "10px", border: "1px solid #ddd", textAlign: "center" }}>
                  {p.available ? (
                    <span style={{ color: "#28a745", fontWeight: "bold" }}>Sí</span>
                  ) : (
                    <span style={{ color: "#dc3545", fontWeight: "bold" }}>No</span>
                  )}
                </td>
                {/* <td style={{ padding: "10px", border: "1px solid #ddd", textAlign: "center" }}>
                  {p.ingredientes?.map((ing) => ing.name).join(", ") || "—"}
                </td> */}
                <td style={{ padding: "10px", border: "1px solid #ddd", textAlign: "center" }}>
                  <button
                    onClick={() => handleDelete(p.id)}
                    style={{
                      backgroundColor: "#dc3545",
                      color: "white",
                      border: "none",
                      borderRadius: "3px",
                      padding: "5px 10px",
                      cursor: "pointer",
                    }}
                  >
                    Eliminar
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        
        ) : (
          <p>No hay productos registrados.</p>
        )}
      </div>
    
  );
};

// ==== estilos reutilizables ====

const btnBlue = {
  marginBottom: "20px",
  padding: "10px 20px",
  backgroundColor: "#007bff",
  color: "white",
  border: "none",
  borderRadius: "4px",
};
const btnRed = {
  backgroundColor: "#dc3545",
  color: "white",
  border: "none",
  borderRadius: "4px",
  padding: "10px 20px",
  marginBottom: "20px",

};

// ==== formulario de creación ====
const ProductForm = ({
  onSubmit,
  onCancel,
  isSubmitting,
  error,
  ingredientes,
}: {
  onSubmit: (data: ProductCreateRequest) => void;
  onCancel: () => void;
  isSubmitting: boolean;
  error: Error | null;
  ingredientes: Ingredient[];
}) => {
  const [formData, setFormData] = useState<ProductCreateRequest>({
    name: "",
    description: "",
    price: 0,
    ingredientesIds: [],
  });

  const handleCheckboxChange = (id: number) => {
    setFormData((prev) => {
      const selected = prev.ingredientesIds.includes(id)
        ? prev.ingredientesIds.filter((x) => x !== id)
        : [...prev.ingredientesIds, id];
      return { ...prev, ingredientesIds: selected };
    });
  };

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
      backgroundColor: '#2E3C50', 
        marginBottom: "20px",
        maxWidth: "500px",
      }}
    >
      <h3>Crear nuevo producto</h3>
      {error && <p style={{ color: "red" }}>Error: {error.message}</p>}

      <input
        type="text"
        placeholder="Nombre"
        value={formData.name}
        onChange={(e) =>
          setFormData({ ...formData, name: e.target.value })
        }
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

      <label><strong>Seleccionar ingredientes:</strong></label>
      <div
        style={{
          display: "grid",
          gridTemplateColumns: "repeat(auto-fit, minmax(200px, 1fr))",
          gap: "5px",
          maxHeight: "150px",
          overflowY: "auto",
          border: "1px solid #ccc",
          padding: "5px",
          borderRadius: "4px",
          backgroundColor: '#2E3C50', 
          marginBottom: "10px",
        }}
      >
        {ingredientes.map((ing) => (
          <label key={ing.id}>
            <input
              type="checkbox"
              checked={formData.ingredientesIds.includes(ing.id)}
              onChange={() => handleCheckboxChange(ing.id)}
            />
            {" "}{ing.name}
          </label>
        ))}
      </div>

      <div style={{ display: "flex", gap: "10px", marginTop: "10px" }}>
        <button
          type="submit"
          disabled={isSubmitting}
          style={{
            ...btnBlue,
            cursor: isSubmitting ? "not-allowed" : "pointer",
          }}
        >
          {isSubmitting ? "Guardando..." : "Crear"}
        </button>
        <button type="button" 
        style={{
          ...btnRed,
        }}
        onClick={onCancel} >
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
