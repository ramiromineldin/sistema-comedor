import { useState } from "react";
import { useKitchenStaffList, useCreateKitchenStaff, useUpdateKitchenStaff, useDeleteKitchenStaff } from "@/services/AdminServices";
import type { KitchenStaffUser, KitchenStaffCreateRequest, KitchenStaffUpdateRequest } from "@/services/AdminServices";

export const AdminUsers = () => {
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [editingUser, setEditingUser] = useState<KitchenStaffUser | null>(null);

  const { data: kitchenStaff, isLoading, error } = useKitchenStaffList();
  const createMutation = useCreateKitchenStaff();
  const updateMutation = useUpdateKitchenStaff();
  const deleteMutation = useDeleteKitchenStaff();

  const handleCreate = (data: KitchenStaffCreateRequest) => {
    createMutation.mutate(data, {
      onSuccess: () => {
        setShowCreateForm(false);
      },
    });
  };

  const handleUpdate = (id: number, data: KitchenStaffUpdateRequest) => {
    updateMutation.mutate({ id, data }, {
      onSuccess: () => {
        setEditingUser(null);
      },
    });
  };

  const handleDelete = (id: number) => {
    if (confirm("¿Estás seguro de que quieres eliminar este usuario?")) {
      deleteMutation.mutate(id);
    }
  };

  if (isLoading) return <div>Cargando...</div>;
  if (error) return <div>Error: {error.message}</div>;

  return (
    
      <div style={{ padding: '20px' }}>
        <h1>Administración de Personal de Cocina</h1>
        
        <button 
          onClick={() => setShowCreateForm(true)}
          style={{ marginBottom: '20px', padding: '10px 20px', backgroundColor: '#007bff', color: 'white', border: 'none', borderRadius: '4px' }}
        >
          Agregar Nuevo Usuario de Cocina
        </button>

        {showCreateForm && (
          <KitchenStaffForm
            onSubmit={handleCreate}
            onCancel={() => setShowCreateForm(false)}
            isSubmitting={createMutation.isPending}
            error={createMutation.error}
          />
        )}

        {editingUser && (
          <KitchenStaffForm
            user={editingUser}
            onSubmit={(data) => handleUpdate(editingUser.id, data)}
            onCancel={() => setEditingUser(null)}
            isSubmitting={updateMutation.isPending}
            error={updateMutation.error}
            isEditing
          />
        )}

        <div>
          <h2>Lista de Personal de Cocina</h2>
          {kitchenStaff && kitchenStaff.length > 0 ? (
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
               <th style={{ padding: "12px", border: "1px solid #ddd", textAlign: "left" }}>Username</th>
               <th style={{ padding: "12px", border: "1px solid #ddd", textAlign: "left" }}>Nombre</th>
               <th style={{ padding: "12px", border: "1px solid #ddd", textAlign: "left" }}>Apellido</th>
               <th style={{ padding: "12px", border: "1px solid #ddd", textAlign: "left" }}>Email</th>
               <th style={{ padding: "12px", border: "1px solid #ddd", textAlign: "center" }}>Edad</th>
               <th style={{ padding: "12px", border: "1px solid #ddd", textAlign: "center" }}>Género</th>
               <th style={{ padding: "12px", border: "1px solid #ddd", textAlign: "center" }}>Acciones</th>
             </tr>
           </thead>
         
           <tbody>
             {kitchenStaff.map((user, index) => (
               <tr
                 key={user.id}
                 style={{
                   backgroundColor: index % 2 === 0 ? "#f9f9f9" : "#ffffff",
                   color: "#333",
                 }}
               >
                 <td style={{ padding: "10px", border: "1px solid #ddd" }}>{user.username}</td>
                 <td style={{ padding: "10px", border: "1px solid #ddd" }}>{user.nombre}</td>
                 <td style={{ padding: "10px", border: "1px solid #ddd" }}>{user.apellido}</td>
                 <td style={{ padding: "10px", border: "1px solid #ddd" }}>{user.email}</td>
                 <td style={{ padding: "10px", border: "1px solid #ddd", textAlign: "center" }}>{user.edad}</td>
                 <td style={{ padding: "10px", border: "1px solid #ddd", textAlign: "center" }}>{user.genero}</td>
                 <td style={{ padding: "10px", border: "1px solid #ddd", textAlign: "center" }}>
                   <button
                     onClick={() => setEditingUser(user)}
                     style={{
                       marginRight: "5px",
                       padding: "5px 10px",
                       backgroundColor: "#28a745",
                       color: "white",
                       border: "none",
                       borderRadius: "3px",
                       cursor: "pointer",
                     }}
                   >
                     Editar
                   </button>
                   <button
                     onClick={() => handleDelete(user.id)}
                     style={{
                       padding: "5px 10px",
                       backgroundColor: "#dc3545",
                       color: "white",
                       border: "none",
                       borderRadius: "3px",
                       cursor: "pointer",
                     }}
                     disabled={deleteMutation.isPending}
                   >
                     Eliminar
                   </button>
                 </td>
               </tr>
             ))}
           </tbody>
         </table>
                   ) : (
            <p>No hay personal de cocina registrado.</p>
          )}
        </div>
      </div>
    
  );
};

const KitchenStaffForm = ({ 
  user, 
  onSubmit, 
  onCancel, 
  isSubmitting, 
  error, 
  isEditing = false 
}: {
  user?: KitchenStaffUser;
  onSubmit: (data: any) => void;
  onCancel: () => void;
  isSubmitting: boolean;
  error: Error | null;
  isEditing?: boolean;
}) => {
  const [formData, setFormData] = useState({
    username: user?.username || "",
    password: "",
    nombre: user?.nombre || "",
    apellido: user?.apellido || "",
    email: user?.email || "",
    edad: user?.edad || 0,
    genero: user?.genero || "",
    domicilio: user?.domicilio || "",
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (isEditing) {
      const { username, password, ...updateData } = formData;
      onSubmit(updateData);
    } else {
      onSubmit(formData);
    }
  };

  return (
    <div style={{ 
      padding: '20px', 
      borderRadius: '4px', 
      backgroundColor: '#2E3C50', 
      marginBottom: '20px' 
    }}>
      <h3>{isEditing ? 'Editar Usuario' : 'Crear Nuevo Usuario de Cocina'}</h3>
      
      {error && (
        <div style={{ 
          color: '#dc3545', 
          backgroundColor: '#f8d7da', 
          padding: '10px', 
          borderRadius: '4px', 
          marginBottom: '10px' 
        }}>
          Error: {error.message}
        </div>
      )}

      <form onSubmit={handleSubmit} style={{ display: 'grid', gap: '10px', maxWidth: '400px' }}>
        {!isEditing && (
          <>
            <input
              type="text"
              placeholder="Username"
              value={formData.username}
              onChange={(e) => setFormData({ ...formData, username: e.target.value })}
              required
              style={{ padding: '8px', border: '1px solid #ccc', borderRadius: '4px' }}
            />
            <input
              type="password"
              placeholder="Password"
              value={formData.password}
              onChange={(e) => setFormData({ ...formData, password: e.target.value })}
              required
              style={{ padding: '8px', border: '1px solid #ccc', borderRadius: '4px' }}
            />
          </>
        )}
        
        <input
          type="text"
          placeholder="Nombre"
          value={formData.nombre}
          onChange={(e) => setFormData({ ...formData, nombre: e.target.value })}
          required
          style={{ padding: '8px', border: '1px solid #ccc', borderRadius: '4px' }}
        />
        <input
          type="text"
          placeholder="Apellido"
          value={formData.apellido}
          onChange={(e) => setFormData({ ...formData, apellido: e.target.value })}
          required
          style={{ padding: '8px', border: '1px solid #ccc', borderRadius: '4px' }}
        />
        <input
          type="email"
          placeholder="Email"
          value={formData.email}
          onChange={(e) => setFormData({ ...formData, email: e.target.value })}
          required
          style={{ padding: '8px', border: '1px solid #ccc', borderRadius: '4px' }}
        />
        <input
          type="number"
          placeholder="Edad"
          value={formData.edad}
          onChange={(e) => setFormData({ ...formData, edad: parseInt(e.target.value) || 0 })}
          required
          min="1"
          style={{ padding: '8px', border: '1px solid #ccc', borderRadius: '4px' }}
        />
        <select
          value={formData.genero}
          onChange={(e) => setFormData({ ...formData, genero: e.target.value })}
          required
          style={{ padding: '8px', border: '1px solid #ccc', borderRadius: '4px' }}
        >
          <option value="">Seleccionar género</option>
          <option value="MASCULINO">Masculino</option>
          <option value="FEMENINO">Femenino</option>
          <option value="OTRO">Otro</option>
        </select>
        <input
          type="text"
          placeholder="Domicilio"
          value={formData.domicilio}
          onChange={(e) => setFormData({ ...formData, domicilio: e.target.value })}
          required
          style={{ padding: '8px', border: '1px solid #ccc', borderRadius: '4px' }}
        />
        
        <div style={{ display: 'flex', gap: '10px', marginTop: '10px' }}>
          <button
            type="submit"
            disabled={isSubmitting}
            style={{
              padding: '10px 20px',
              backgroundColor: '#007bff',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: isSubmitting ? 'not-allowed' : 'pointer'
            }}
          >
            {isSubmitting ? 'Guardando...' : (isEditing ? 'Actualizar' : 'Crear')}
          </button>
          <button
            type="button"
            onClick={onCancel}
            style={{
              padding: '10px 20px',
              backgroundColor: '#6c757d',
              color: 'white',
              border: 'none',
              borderRadius: '4px'
            }}
          >
            Cancelar
          </button>
        </div>
      </form>
    </div>
  );
};