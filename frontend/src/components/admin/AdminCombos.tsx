import React, { useState } from "react";
import {
  useComboList,
  useCreateCombo,
  useDeleteCombo,
  useProductList,
  useUpdateComboDiscount
} from "@/services/AdminServices";
import type { ComboCreateRequest, Product, Combo } from "@/services/AdminServices";

import styles from "./AdminCombos.module.css";

export const AdminCombos = () => {
  const [showForm, setShowForm] = useState(false);
  const { data: combos, isLoading, error, refetch } = useComboList();
  const { data: productos } = useProductList();
  const createMutation = useCreateCombo();
  const deleteMutation = useDeleteCombo();

  const handleCreate = (data: ComboCreateRequest) => {
    createMutation.mutate(data, {
      onSuccess: () => setShowForm(false),
    });
  };

  const handleDelete = (id: number) => {
    if (confirm("¿Seguro que querés eliminar este combo?")) {
      deleteMutation.mutate(id);
    }
  };

  if (isLoading) return <div className={styles.loading}>Cargando combos...</div>;

  if (error)
    return <div className={styles.error}>Error: {error.message}</div>;

  return (
    <div className={styles.container}>
      <h1>Gestión de Combos</h1>

      <button onClick={() => setShowForm(true)} className={styles.btnBlue}>
        Nuevo Combo
      </button>

      {showForm && (
        <ComboForm
          onSubmit={handleCreate}
          onCancel={() => setShowForm(false)}
          isSubmitting={createMutation.isPending}
          error={createMutation.error}
          productos={productos || []}
        />
      )}

      <h2>Lista de Combos</h2>

      {combos && combos.length > 0 ? (
        <table className={styles.table}>
          <thead>
            <tr>
              <th className={styles.th}>Nombre</th>
              <th className={styles.th}>Descripción</th>
              <th className={styles.th}>Precio</th>
              <th className={styles.th}>Descuento</th>
              <th className={styles.th}>Productos</th>
              <th className={styles.th}>Acciones</th>
            </tr>
          </thead>

          <tbody>
            {combos.map((combo) => (
              <ComboRow
                key={combo.id}
                combo={combo}
                onDelete={handleDelete}
                onUpdateSuccess={() => refetch()}
              />
            ))}
          </tbody>
        </table>
      ) : (
        <p>No hay combos registrados.</p>
      )}
    </div>
  );
};

const ComboRow = ({
  combo,
  onDelete,
  onUpdateSuccess,
}: {
  combo: Combo;
  onDelete: (id: number) => void;
  onUpdateSuccess: () => void;
}) => {
  const [discount, setDiscount] = useState<number | null>(null);
  const updateMutation = useUpdateComboDiscount();

  const handleUpdateDiscount = () => {
    if (discount === null || isNaN(discount))
      return alert("Ingrese un valor válido");

    if (discount < 0 || discount > 100)
      return alert("El descuento debe estar entre 0 y 100");

    updateMutation.mutate(
      { id: combo.id, discount },
      {
        onSuccess: () => {
          alert("Descuento actualizado correctamente");
          setDiscount(null);
          onUpdateSuccess();
        },
        onError: (error: Error) => {
          alert(`Error: ${error.message}`);
        },
      }
    );
  };

  return (
    <tr>
      <td className={styles.td}>{combo.name}</td>
      <td className={styles.td}>{combo.description}</td>
      <td className={styles.td}>${combo.price}</td>

      <td className={styles.td}>
        <div className={styles.discountActual}>
          <strong>Actual: {(combo.discount * 100).toFixed(0)}%</strong>
        </div>

        <div className={styles.discountEdit}>
          <input
            type="number"
            step="1"
            min="0"
            max="100"
            value={discount ?? ""}
            className={styles.discountInput}
            onChange={(e) =>
              setDiscount(e.target.value ? parseFloat(e.target.value) : null)
            }
          />

          <button
            onClick={handleUpdateDiscount}
            disabled={updateMutation.isPending}
            className={styles.btnGreen}
          >
            {updateMutation.isPending ? "..." : "Actualizar"}
          </button>
        </div>

        <small className={styles.hint}>(Ingrese porcentaje: 25 = 25%)</small>
      </td>

      <td className={styles.td}>
        {combo.subAlimentos?.map((p) => p.name).join(", ") || "—"}
      </td>

      <td className={styles.td}>
        <button onClick={() => onDelete(combo.id)} className={styles.btnRed}>
          Eliminar
        </button>
      </td>
    </tr>
  );
};

const ComboForm = ({
  onSubmit,
  onCancel,
  isSubmitting,
  error,
  productos,
}: {
  onSubmit: (data: ComboCreateRequest) => void;
  onCancel: () => void;
  isSubmitting: boolean;
  error: Error | null;
  productos: Product[];
}) => {
  const [formData, setFormData] = useState<ComboCreateRequest>({
    name: "",
    description: "",
    discount: 0,
    alimentoIds: [],
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit(formData);
  };

  const toggle = (id: number) => {
    setFormData((prev) => {
      const selected = prev.alimentoIds.includes(id)
        ? prev.alimentoIds.filter((x) => x !== id)
        : [...prev.alimentoIds, id];
      return { ...prev, alimentoIds: selected };
    });
  };

  return (
    <form className={styles.form} onSubmit={handleSubmit}>
      <h3>Crear nuevo combo</h3>

      {error && <p className={styles.error}>Error: {error.message}</p>}

      <input
        type="text"
        placeholder="Nombre"
        required
        className={styles.input}
        value={formData.name}
        onChange={(e) => setFormData({ ...formData, name: e.target.value })}
      />

      <input
        type="text"
        placeholder="Descripción"
        required
        className={styles.input}
        value={formData.description}
        onChange={(e) => setFormData({ ...formData, description: e.target.value })}
      />

      <input
        type="number"
        placeholder="Descuento (ej: 25)"
        min="0"
        max="100"
        className={styles.input}
        value={formData.discount * 100}
        onChange={(e) =>
          setFormData({ ...formData, discount: (parseFloat(e.target.value) || 0) / 100 })
        }
      />

      <label><strong>Seleccionar productos:</strong></label>

      <div className={styles.productList}>
        {productos.map((p) => (
          <label key={p.id}>
            <input
              type="checkbox"
              checked={formData.alimentoIds.includes(p.id)}
              onChange={() => toggle(p.id)}
            />
            {" "}{p.name}
          </label>
        ))}
      </div>

      <div className={styles.formButtons}>
        <button type="submit" disabled={isSubmitting} className={styles.btnBlue}>
          {isSubmitting ? "Guardando..." : "Crear"}
        </button>

        <button type="button" onClick={onCancel} className={styles.btnRed}>
          Cancelar
        </button>
      </div>
    </form>
  );
};
