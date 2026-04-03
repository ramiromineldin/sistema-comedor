import { useState } from "react";
import {
  usePromotionList,
  useCreatePromotion,
  useDeletePromotion,
  useActivatePromotion,
  useDeactivatePromotion,
  useProductList,
  type PromotionResponse,
  type PromotionCreateRequest,
  type Product,
  type PromotionType,
  type DayOfWeek,
} from "@/services/AdminServices";

// Helper para obtener el label del tipo de promoción
const getPromotionTypeLabel = (type: PromotionType): string => {
  const labels: Record<PromotionType, string> = {
    PERCENTAGE_DISCOUNT: "Descuento porcentual temporal",
    RECURRING_DAY_DISCOUNT: "Descuento recurrente por día",
    AMOUNT_DISCOUNT: "Descuento por monto mínimo",
    BUY_X_PAY_Y: "Compra X paga Y (NxM)",
    TIME_RESTRICTED_BUY_X_PAY_Y: "Compra X paga Y (horario restringido)",
  };
  return labels[type];
};

// Helper para obtener nombre del día en español
const getDayNameInSpanish = (day: DayOfWeek): string => {
  const dayNames: Record<DayOfWeek, string> = {
    MONDAY: "Lunes",
    TUESDAY: "Martes",
    WEDNESDAY: "Miércoles",
    THURSDAY: "Jueves",
    FRIDAY: "Viernes",
    SATURDAY: "Sábado",
    SUNDAY: "Domingo",
  };
  return dayNames[day];
};

// Helper para obtener los detalles de la promoción según su tipo
const getPromotionDetails = (promo: PromotionResponse): string => {
  switch (promo.type) {
    case "PERCENTAGE_DISCOUNT":
      return `${promo.discountPercentage}% de descuento`;
    case "RECURRING_DAY_DISCOUNT": {
      const dayName = promo.recurringDay
        ? getDayNameInSpanish(promo.recurringDay)
        : "";
      return `${promo.discountPercentage}% de descuento todos los ${dayName}`;
    }
    case "AMOUNT_DISCOUNT":
      return `$${promo.discountAmount} OFF en compras mayores a $${promo.minimumPurchaseAmount}`;
    case "BUY_X_PAY_Y":
      return `Lleva ${promo.buyQuantity}, paga ${promo.payQuantity}`;
    case "TIME_RESTRICTED_BUY_X_PAY_Y": {
      const horario = promo.startTime && promo.endTime 
        ? ` (${promo.startTime.slice(0, 5)} - ${promo.endTime.slice(0, 5)})`
        : "";
      return `Lleva ${promo.buyQuantity}, paga ${promo.payQuantity}${horario}`;
    }
    default:
      return "";
  }
};

const btnBase = {
  border: "none",
  borderRadius: "4px",
  cursor: "pointer",
  fontWeight: "500" as const,
};

const btnBlue = {
  ...btnBase,
  backgroundColor: "#3498db",
  color: "white",
  padding: "10px 20px",
  marginBottom: "20px",
  fontSize: "14px",
};

const th = {
  padding: "12px",
  border: "1px solid #ddd",
  textAlign: "left" as const,
};

const td = {
  padding: "12px",
  border: "1px solid #ddd",
  color: "#333",
};

const input = {
  width: "100%",
  padding: "10px",
  marginBottom: "10px",
  borderRadius: "4px",
  border: "1px solid #ddd",
  fontSize: "14px",
};

export const AdminPromotions = () => {
  const [showForm, setShowForm] = useState(false);
  const { data: promociones, isLoading, error } = usePromotionList();
  const { data: productos } = useProductList();
  const createMutation = useCreatePromotion();
  const deleteMutation = useDeletePromotion();
  const activateMutation = useActivatePromotion();
  const deactivateMutation = useDeactivatePromotion();

  const handleCreate = (data: PromotionCreateRequest) => {
    createMutation.mutate(data, {
      onSuccess: () => setShowForm(false),
    });
  };

  const handleDelete = (id: number) => {
    if (confirm("¿Seguro querés eliminar la promoción?")) {
      deleteMutation.mutate(id);
    }
  };

  const handleToggleActive = (promo: PromotionResponse) => {
    if (promo.active) deactivateMutation.mutate(promo.id);
    else activateMutation.mutate(promo.id);
  };

  if (isLoading)
    return (
      
        <div style={{ padding: "20px" }}>Cargando promociones...</div>
      
    );
  if (error)
    return (
      
        <div style={{ padding: "20px", color: "red" }}>
          Error al cargar promociones
        </div>
      
    );

  return (
    
      <div style={{ padding: "20px" }}>
        <h1>Gestión de Promociones</h1>

        <button onClick={() => setShowForm(true)} style={btnBlue}>
          Nueva Promoción
        </button>

        {showForm && (
          <PromotionForm
            onSubmit={handleCreate}
            onCancel={() => setShowForm(false)}
            isSubmitting={createMutation.isPending}
            error={createMutation.error}
            productos={productos || []}
          />
        )}

        <h2>Lista de Promociones</h2>
        {promociones && promociones.length > 0 ? (
          <table
            style={{
              width: "100%",
              borderCollapse: "collapse",
              marginTop: "10px",
              backgroundColor: "#ffffff",
              borderRadius: "8px",
              overflow: "hidden",
            }}
          >
            <thead>
              <tr style={{ backgroundColor: "#2E3C50", color: "white" }}>
                <th style={th}>Nombre</th>
                <th style={th}>Tipo</th>
                <th style={th}>Detalle</th>
                <th style={th}>Vigencia</th>
                <th style={th}>Estado</th>
                <th style={th}>Productos</th>
                <th style={th}>Acciones</th>
              </tr>
            </thead>

            <tbody>
              {promociones.map((promo, index) => (
                <tr
                  key={promo.id}
                  style={{
                    backgroundColor: index % 2 === 0 ? "#f9f9f9" : "#ffffff",
                  }}
                >
                  <td style={td}>
                    <strong>{promo.name}</strong>
                    <div
                      style={{
                        fontSize: "0.85rem",
                        color: "#666",
                        marginTop: "4px",
                      }}
                    >
                      {promo.description}
                    </div>
                  </td>

                  <td style={td}>
                    <span
                      style={{
                        fontSize: "0.85rem",
                        padding: "4px 8px",
                        backgroundColor: "#3498db",
                        color: "white",
                        borderRadius: "4px",
                        display: "inline-block",
                      }}
                    >
                      {getPromotionTypeLabel(promo.type)}
                    </span>
                  </td>

                  <td style={td}>
                    <div style={{ fontSize: "0.9rem", fontWeight: "500" }}>
                      {getPromotionDetails(promo)}
                    </div>
                  </td>

                  <td style={td}>
                    <div style={{ fontSize: "0.85rem" }}>
                      <div>{new Date(promo.startDate).toLocaleDateString()}</div>
                      <div>{new Date(promo.endDate).toLocaleDateString()}</div>
                    </div>
                  </td>

                  <td style={td}>
                    {promo.active ? (
                      <span
                        style={{
                          padding: "4px 8px",
                          backgroundColor: "#2ecc71",
                          color: "white",
                          borderRadius: "4px",
                          fontSize: "0.85rem",
                        }}
                      >
                        Activa
                      </span>
                    ) : (
                      <span
                        style={{
                          padding: "4px 8px",
                          backgroundColor: "#e74c3c",
                          color: "white",
                          borderRadius: "4px",
                          fontSize: "0.85rem",
                        }}
                      >
                        Inactiva
                      </span>
                    )}
                  </td>

                  <td style={td}>
                    <details>
                      <summary style={{ cursor: "pointer", color: "#3498db" }}>
                        Ver ({promo.items?.length ?? 0})
                      </summary>
                      <ul
                        style={{
                          margin: "5px 0",
                          paddingLeft: "20px",
                          fontSize: "0.85rem",
                        }}
                      >
                        {promo.items?.length === 0 &&
                          promo.type === "AMOUNT_DISCOUNT" && (
                            <li>
                              Sin productos: se aplica sobre el total del carrito
                            </li>
                          )}

                        {(promo.items ?? []).map((item) => (
                          <li key={item.id}>
                            {item.productName} - $
                            {item.originalPrice.toFixed(2)}
                          </li>
                        ))}
                      </ul>
                    </details>
                  </td>

                  <td style={td}>
                    <button
                      onClick={() => handleToggleActive(promo)}
                      style={{
                        ...btnBase,
                        backgroundColor: promo.active ? "#f39c12" : "#2ecc71",
                        color: "white",
                        padding: "6px 10px",
                        marginRight: "6px",
                        fontSize: "0.85rem",
                      }}
                    >
                      {promo.active ? "Desactivar" : "Activar"}
                    </button>

                    <button
                      onClick={() => handleDelete(promo.id)}
                      style={{
                        ...btnBase,
                        backgroundColor: "#e74c3c",
                        color: "white",
                        padding: "6px 10px",
                        fontSize: "0.85rem",
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
          <p>No hay promociones creadas.</p>
        )}
      </div>
   
  );
};

const PromotionForm = ({
  onSubmit,
  onCancel,
  isSubmitting,
  error,
  productos,
}: {
  onSubmit: (data: PromotionCreateRequest) => void;
  onCancel: () => void;
  isSubmitting: boolean;
  error: Error | null;
  productos: Product[];
}) => {
  // ✅ SIN active, porque PromotionCreateRequest no lo tiene
  const [formData, setFormData] = useState<PromotionCreateRequest>({
    name: "",
    description: "",
    startDate: new Date().toISOString().split("T")[0],
    endDate: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000)
      .toISOString()
      .split("T")[0],
    type: "PERCENTAGE_DISCOUNT",
    discountPercentage: undefined,
    discountAmount: undefined,
    minimumPurchaseAmount: undefined,
    buyQuantity: undefined,
    payQuantity: undefined,
    recurringDay: undefined,
    startTime: undefined,
    endTime: undefined,
    items: [],
  });

  const handleCheckboxChange = (productId: number) => {
    setFormData((prev) => {
      const exists = prev.items.some((item) => item.productId === productId);
      const items = exists
        ? prev.items.filter((item) => item.productId !== productId)
        : [...prev.items, { productId }];
      return { ...prev, items };
    });
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    // ✅ solo exige items si no es AMOUNT_DISCOUNT
    if (formData.type !== "AMOUNT_DISCOUNT" && formData.items.length === 0) {
      alert("Debes seleccionar al menos un producto");
      return;
    }

    onSubmit(formData);
  };

  const daysOfWeek: DayOfWeek[] = [
    "MONDAY",
    "TUESDAY",
    "WEDNESDAY",
    "THURSDAY",
    "FRIDAY",
    "SATURDAY",
    "SUNDAY",
  ];
  const dayLabels: Record<DayOfWeek, string> = {
    MONDAY: "Lunes",
    TUESDAY: "Martes",
    WEDNESDAY: "Miércoles",
    THURSDAY: "Jueves",
    FRIDAY: "Viernes",
    SATURDAY: "Sábado",
    SUNDAY: "Domingo",
  };

  return (
    <form
      onSubmit={handleSubmit}
      style={{
        border: "1px solid #ddd",
        padding: "20px",
        borderRadius: "4px",
        backgroundColor: "#2E3C50",
        color: "white",
        marginBottom: "20px",
        maxWidth: "900px",
      }}
    >
      <h3 style={{ marginTop: 0 }}>Crear nueva promoción</h3>
      {error && <p style={{ color: "#ff6b6b" }}>Error: {error.message}</p>}

      <input
        type="text"
        placeholder="Nombre de la promoción"
        value={formData.name}
        onChange={(e) => setFormData({ ...formData, name: e.target.value })}
        required
        style={input}
      />

      <textarea
        placeholder="Descripción"
        value={formData.description}
        onChange={(e) =>
          setFormData({ ...formData, description: e.target.value })
        }
        style={{ ...input, minHeight: "60px", resize: "vertical" as const }}
      />

      {/* Selector de tipo de promoción */}
      <div style={{ marginBottom: "15px" }}>
        <label
          style={{
            display: "block",
            marginBottom: "8px",
            fontWeight: "500",
          }}
        >
          Tipo de Promoción
        </label>
        <select
          value={formData.type}
          onChange={(e) => {
            const t = e.target.value as PromotionType;
            setFormData({
              ...formData,
              type: t,
              items: t === "AMOUNT_DISCOUNT" ? [] : formData.items,
            });
          }}
          style={{ ...input, marginBottom: "5px" }}
        >
          <option value="PERCENTAGE_DISCOUNT">
            Descuento porcentual temporal
          </option>
          <option value="RECURRING_DAY_DISCOUNT">
            Descuento recurrente por día
          </option>
          <option value="AMOUNT_DISCOUNT">Descuento por monto mínimo</option>
          <option value="BUY_X_PAY_Y">Compra X paga Y (NxM)</option>
          <option value="TIME_RESTRICTED_BUY_X_PAY_Y">
            Compra X paga Y (con restricción horaria)
          </option>
        </select>
      </div>

      {/* Campos específicos según el tipo */}
      {formData.type === "PERCENTAGE_DISCOUNT" && (
        <div
          style={{
            backgroundColor: "#34495e",
            padding: "15px",
            borderRadius: "8px",
            marginBottom: "15px",
          }}
        >
          <h4 style={{ margin: "0 0 10px 0" }}>
            Configuración de Descuento Porcentual Temporal
          </h4>
          <label style={{ display: "block", marginBottom: "5px" }}>
            Porcentaje de descuento (%)
          </label>
          <input
            type="number"
            min={1}
            max={100}
            value={formData.discountPercentage ?? ""}
            onChange={(e) =>
              setFormData({
                ...formData,
                discountPercentage: Number(e.target.value),
              })
            }
            required
            style={{ ...input, marginBottom: 0 }}
          />
        </div>
      )}

      {formData.type === "RECURRING_DAY_DISCOUNT" && (
        <div
          style={{
            backgroundColor: "#34495e",
            padding: "15px",
            borderRadius: "8px",
            marginBottom: "15px",
          }}
        >
          <h4 style={{ margin: "0 0 10px 0" }}>
            Configuración de Descuento Recurrente
          </h4>

          <label style={{ display: "block", marginBottom: "5px" }}>
            Día de la semana
          </label>
          <select
            value={formData.recurringDay ?? ""}
            onChange={(e) =>
              setFormData({
                ...formData,
                recurringDay: e.target.value as DayOfWeek,
              })
            }
            required
            style={input}
          >
            <option value="" disabled>
              Seleccionar día
            </option>
            {daysOfWeek.map((d) => (
              <option key={d} value={d}>
                {dayLabels[d]}
              </option>
            ))}
          </select>

          <label style={{ display: "block", marginBottom: "5px" }}>
            Porcentaje de descuento (%)
          </label>
          <input
            type="number"
            min={1}
            max={100}
            value={formData.discountPercentage ?? ""}
            onChange={(e) =>
              setFormData({
                ...formData,
                discountPercentage: Number(e.target.value),
              })
            }
            required
            style={{ ...input, marginBottom: 0 }}
          />
        </div>
      )}

      {formData.type === "AMOUNT_DISCOUNT" && (
        <div
          style={{
            backgroundColor: "#34495e",
            padding: "15px",
            borderRadius: "8px",
            marginBottom: "15px",
          }}
        >
          <h4 style={{ margin: "0 0 10px 0" }}>
            Configuración de Descuento por Monto Mínimo (carrito)
          </h4>

          <label style={{ display: "block", marginBottom: "5px" }}>
            Monto mínimo de compra
          </label>
          <input
            type="number"
            min={1}
            value={formData.minimumPurchaseAmount ?? ""}
            onChange={(e) =>
              setFormData({
                ...formData,
                minimumPurchaseAmount: Number(e.target.value),
              })
            }
            required
            style={input}
          />

          <label style={{ display: "block", marginBottom: "5px" }}>
            Descuento fijo ($)
          </label>
          <input
            type="number"
            min={1}
            value={formData.discountAmount ?? ""}
            onChange={(e) =>
              setFormData({
                ...formData,
                discountAmount: Number(e.target.value),
              })
            }
            required
            style={{ ...input, marginBottom: 0 }}
          />
        </div>
      )}

      {formData.type === "BUY_X_PAY_Y" && (
        <div
          style={{
            backgroundColor: "#34495e",
            padding: "15px",
            borderRadius: "8px",
            marginBottom: "15px",
          }}
        >
          <h4 style={{ margin: "0 0 10px 0" }}>
            Configuración de Compra X Paga Y
          </h4>

          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "10px" }}>
            <div>
              <label style={{ display: "block", marginBottom: "5px" }}>
                Compra (X)
              </label>
              <input
                type="number"
                min={1}
                value={formData.buyQuantity ?? ""}
                onChange={(e) =>
                  setFormData({ ...formData, buyQuantity: Number(e.target.value) })
                }
                required
                style={input}
              />
            </div>
            <div>
              <label style={{ display: "block", marginBottom: "5px" }}>
                Paga (Y)
              </label>
              <input
                type="number"
                min={1}
                value={formData.payQuantity ?? ""}
                onChange={(e) =>
                  setFormData({ ...formData, payQuantity: Number(e.target.value) })
                }
                required
                style={input}
              />
            </div>
          </div>
        </div>
      )}

      {formData.type === "TIME_RESTRICTED_BUY_X_PAY_Y" && (
        <div
          style={{
            backgroundColor: "#34495e",
            padding: "15px",
            borderRadius: "8px",
            marginBottom: "15px",
          }}
        >
          <h4 style={{ margin: "0 0 10px 0" }}>
            Configuración de Compra X Paga Y (con restricción horaria)
          </h4>

          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "10px", marginBottom: "15px" }}>
            <div>
              <label style={{ display: "block", marginBottom: "5px" }}>
                Compra (X)
              </label>
              <input
                type="number"
                min={1}
                value={formData.buyQuantity ?? ""}
                onChange={(e) =>
                  setFormData({ ...formData, buyQuantity: Number(e.target.value) })
                }
                required
                style={input}
              />
            </div>
            <div>
              <label style={{ display: "block", marginBottom: "5px" }}>
                Paga (Y)
              </label>
              <input
                type="number"
                min={1}
                value={formData.payQuantity ?? ""}
                onChange={(e) =>
                  setFormData({ ...formData, payQuantity: Number(e.target.value) })
                }
                required
                style={input}
              />
            </div>
          </div>

          <h5 style={{ margin: "15px 0 10px 0", color: "#3498db" }}>
            ⏰ Horario de aplicación
          </h5>
          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "10px" }}>
            <div>
              <label style={{ display: "block", marginBottom: "5px" }}>
                Hora de inicio
              </label>
              <input
                type="time"
                value={formData.startTime ?? ""}
                onChange={(e) =>
                  setFormData({ ...formData, startTime: e.target.value })
                }
                required
                style={input}
              />
            </div>
            <div>
              <label style={{ display: "block", marginBottom: "5px" }}>
                Hora de fin
              </label>
              <input
                type="time"
                value={formData.endTime ?? ""}
                onChange={(e) =>
                  setFormData({ ...formData, endTime: e.target.value })
                }
                required
                style={input}
              />
            </div>
          </div>
          <p style={{ fontSize: "0.85rem", color: "#95a5a6", margin: "8px 0 0 0" }}>
            Ejemplo: "18:00" a "23:59" para aplicar la promoción solo después de las 18hs
          </p>
        </div>
      )}

      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "10px", marginBottom: "15px" }}>
        <div>
          <label style={{ display: "block", marginBottom: "5px", fontWeight: "500" }}>
            Fecha inicio {formData.type === "RECURRING_DAY_DISCOUNT" && "(se ignora para promociones recurrentes)"}
          </label>
          <input
            type="date"
            value={formData.startDate}
            onChange={(e) => setFormData({ ...formData, startDate: e.target.value })}
            required
            style={{ ...input, opacity: formData.type === "RECURRING_DAY_DISCOUNT" ? 0.5 : 1 }}
          />
        </div>
        <div>
          <label style={{ display: "block", marginBottom: "5px", fontWeight: "500" }}>
            Fecha fin {formData.type === "RECURRING_DAY_DISCOUNT" && "(se ignora para promociones recurrentes)"}
          </label>
          <input
            type="date"
            value={formData.endDate}
            onChange={(e) => setFormData({ ...formData, endDate: e.target.value })}
            required
            style={{ ...input, opacity: formData.type === "RECURRING_DAY_DISCOUNT" ? 0.5 : 1 }}
          />
        </div>
      </div>

      {/* Productos SOLO si no es AMOUNT_DISCOUNT */}
      {formData.type !== "AMOUNT_DISCOUNT" && (
        <>
          <label style={{ display: "block", marginTop: "15px", marginBottom: "10px", fontWeight: "500" }}>
            <strong>Productos aplicables:</strong>
          </label>

          <div
            style={{
              display: "grid",
              gridTemplateColumns: "repeat(auto-fit, minmax(250px, 1fr))",
              gap: "10px",
              marginBottom: "15px",
              maxHeight: "250px",
              overflowY: "auto",
              padding: "10px",
              backgroundColor: "white",
              borderRadius: "4px",
              border: "1px solid #ddd",
            }}
          >
            {productos.map((prod) => (
              <label
                key={prod.id}
                style={{
                  display: "flex",
                  alignItems: "center",
                  gap: "8px",
                  padding: "8px",
                  backgroundColor: formData.items.some(
                    (item) => item.productId === prod.id
                  )
                    ? "#e3f2fd"
                    : "transparent",
                  borderRadius: "4px",
                  cursor: "pointer",
                  color: "#333",
                }}
              >
                <input
                  type="checkbox"
                  checked={formData.items.some(
                    (item) => item.productId === prod.id
                  )}
                  onChange={() => handleCheckboxChange(prod.id)}
                />
                <span style={{ fontSize: "14px" }}>
                  {prod.name} - ${prod.price.toFixed(2)}
                </span>
              </label>
            ))}
          </div>
        </>
      )}

      <div style={{ display: "flex", gap: "10px", marginTop: "20px" }}>
        <button
          type="submit"
          disabled={isSubmitting}
          style={{
            ...btnBase,
            backgroundColor: "#3498db",
            color: "white",
            padding: "10px 16px",
          }}
        >
          {isSubmitting ? "Guardando..." : "Guardar"}
        </button>

        <button
          type="button"
          onClick={onCancel}
          style={{
            ...btnBase,
            backgroundColor: "#95a5a6",
            color: "white",
            padding: "10px 16px",
          }}
        >
          Cancelar
        </button>
      </div>
    </form>
  );
};

export default AdminPromotions;