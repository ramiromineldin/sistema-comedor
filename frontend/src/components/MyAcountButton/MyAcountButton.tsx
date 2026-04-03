import { useState, useRef, useEffect } from "react";

interface JwtUserData {
  sub: string; // username
  role: "ADMIN" | "PERSONAL_COCINA" | "USER";
  iat: number;
  exp: number;
}

interface MyAccountButtonProps {
  user: JwtUserData | null;
  onLogout: () => void;
}

export function MyAcountButton({ user, onLogout }: MyAccountButtonProps) {
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  // Cerrar dropdown al hacer click fuera
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setIsOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  if (!user) {
    return (
      <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
        <img 
          src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24' fill='%23666'%3E%3Cpath d='M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z'/%3E%3C/svg%3E"
          alt="User Icon" 
          style={{ borderRadius: "50%", width: "24px", height: "24px" }}
        />
        <span>Usuario</span>
      </div>
    );
  }

  // Icono diferente según el rol
  const getAvatarByRole = (role: string) => {
    switch (role) {
      case 'ADMIN':
        return "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24' fill='%23d4a574'%3E%3Cpath d='M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z'/%3E%3C/svg%3E";
      case 'PERSONAL_COCINA':
        return "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24' fill='%23e74c3c'%3E%3Cpath d='M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z'/%3E%3C/svg%3E";
      case 'USER':
      default:
        return "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24' fill='%233498db'%3E%3Cpath d='M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z'/%3E%3C/svg%3E";
    }
  };

  const handleLogout = () => {
    setIsOpen(false);
    onLogout();
  };

  return (
    <div style={{ position: "relative" }} ref={dropdownRef}>
      {/* Botón principal */}
      <button
        onClick={() => setIsOpen(!isOpen)}
        style={{
          display: "flex",
          alignItems: "center",
          gap: "8px",
          background: "none",
          border: "none",
          color: "inherit",
          cursor: "pointer",
          padding: "8px 12px",
          borderRadius: "6px",
          transition: "background-color 0.2s",
          backgroundColor: isOpen ? "rgba(255, 255, 255, 0.1)" : "transparent"
        }}
        onMouseEnter={(e) => {
          if (!isOpen) {
            e.currentTarget.style.backgroundColor = "rgba(255, 255, 255, 0.05)";
          }
        }}
        onMouseLeave={(e) => {
          if (!isOpen) {
            e.currentTarget.style.backgroundColor = "transparent";
          }
        }}
      >
        <img 
          src={getAvatarByRole(user.role)}
          alt="User Avatar" 
          style={{ borderRadius: "50%", width: "24px", height: "24px" }}
        />
        <span style={{ fontWeight: "500" }}>{user.sub}</span>
        <span style={{ fontSize: "0.8em", color: "#666" }}>
          ({user.role})
        </span>
        {/* Flecha hacia abajo */}
        <svg 
          width="12" 
          height="12" 
          viewBox="0 0 24 24" 
          fill="currentColor"
          style={{ 
            transform: isOpen ? "rotate(180deg)" : "rotate(0deg)",
            transition: "transform 0.2s"
          }}
        >
          <path d="M7 10l5 5 5-5z"/>
        </svg>
      </button>

      {/* Dropdown menu */}
      {isOpen && (
        <div
          style={{
            position: "absolute",
            top: "100%",
            right: "0",
            marginTop: "4px",
            backgroundColor: "white",
            border: "1px solid #ddd",
            borderRadius: "8px",
            boxShadow: "0 4px 12px rgba(0, 0, 0, 0.15)",
            minWidth: "180px",
            zIndex: 1000,
            overflow: "hidden"
          }}
        >
          {/* Header del usuario */}
          <div style={{
            padding: "12px 16px",
            borderBottom: "1px solid #eee",
            backgroundColor: "#f8f9fa"
          }}>
            <div style={{ fontWeight: "600", color: "#333", marginBottom: "4px" }}>
              {user.sub}
            </div>
            <div style={{ fontSize: "0.85em", color: "#666" }}>
              Rol: {user.role}
            </div>
          </div>

          {/* Opciones del menú */}
          <div style={{ padding: "8px 0" }}>
            <button
              onClick={() => {
                setIsOpen(false);
                // Aquí podrías agregar navegación a perfil
                console.log("Ver perfil");
              }}
              style={{
                width: "100%",
                padding: "8px 16px",
                border: "none",
                background: "none",
                textAlign: "left",
                cursor: "pointer",
                color: "#333",
                fontSize: "14px"
              }}
              onMouseEnter={(e) => {
                e.currentTarget.style.backgroundColor = "#f0f0f0";
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.backgroundColor = "transparent";
              }}
            >
              👤 Ver perfil
            </button>

            <button
              onClick={() => {
                setIsOpen(false);
                // Aquí podrías agregar navegación a configuración
                console.log("Configuración");
              }}
              style={{
                width: "100%",
                padding: "8px 16px",
                border: "none",
                background: "none",
                textAlign: "left",
                cursor: "pointer",
                color: "#333",
                fontSize: "14px"
              }}
              onMouseEnter={(e) => {
                e.currentTarget.style.backgroundColor = "#f0f0f0";
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.backgroundColor = "transparent";
              }}
            >
              ⚙️ Configuración
            </button>

            {/* Separador */}
            <div style={{
              height: "1px",
              backgroundColor: "#eee",
              margin: "8px 0"
            }} />

            {/* Logout */}
            <button
              onClick={handleLogout}
              style={{
                width: "100%",
                padding: "8px 16px",
                border: "none",
                background: "none",
                textAlign: "left",
                cursor: "pointer",
                color: "#e74c3c",
                fontSize: "14px",
                fontWeight: "500"
              }}
              onMouseEnter={(e) => {
                e.currentTarget.style.backgroundColor = "#ffeaea";
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.backgroundColor = "transparent";
              }}
            >
              🚪 Cerrar sesión
            </button>
          </div>
        </div>
      )}
    </div>
  );
}