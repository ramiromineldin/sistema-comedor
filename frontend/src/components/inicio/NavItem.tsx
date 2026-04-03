export type NavItem = {
    id: number;
    nombre: string;
    path: string;
}

export const navItems= {
    comun: [
        {
        id: 1,
        nombre: "Inicio",
        path: "/inicio",
    }
    ],

    estudiante:[
    {
        id: 1,
        nombre: "Inicio",
        path: "/inicio",
    },

    {
        id: 2,
        nombre: "Registro",
        path: "/signup",
    },
    ],

    registro:[
        {
            id: 1,
            nombre: "Inicio",
            path: "/inicio",
        },
    
        {
            id: 2,
            nombre: "Ingreso",
            path: "/login-estudiante",
        },
    ],

    pedidos:[
        {
            id: 1,
            nombre: "Historial",
            path: "/cocina/historial",
        },
    
        {
            id: 2,
            nombre: "Menú del día",
            path: "/cocina/menu-del-dia",
        },
        {
            id: 3,
            nombre: "Control de stock",
            path: "/cocina/control-stock",
        },

    ],

    historial:[
        {
            id: 1,
            nombre: "Pedidos activos",
            path: "/cocina",
        },
    
        {
            id: 2,
            nombre: "Menú del día",
            path: "/cocina/menu-del-dia",
        },
        {
            id: 3,
            nombre: "Control de stock",
            path: "/cocina/control-stock",
        },

    ],
    menuDelDia:[
        {
            id: 1,
            nombre: "Pedidos Activos",
            path: "/cocina",
        },
    
        {
            id: 2,
            nombre: "Historial",
            path: "/cocina/historial",
        },
    
        {
            id: 3,
            nombre: "Control de stock",
            path: "/cocina/control-stock",
        },
    ],


    stock:[
        {
            id: 1,
            nombre: "Pedidos Activos",
            path: "/cocina",
        },
    
        {
            id: 2,
            nombre: "Historial",
            path: "/cocina/historial",
        },
    
        {
            id: 3,
            nombre: "Menú del día",
            path: "/cocina/menu-del-dia",
        },

    ],

    
    studentNav:[
        {
            id: 1,
            nombre: "Home",
            path: "/estudiante",
        },
        {
            id: 2,
            nombre: "Menú del día",
            path: "/estudiante/menu-del-dia",
        },
        {
            id: 3,
            nombre: "Mis pedidos",
            path: "/estudiante/mis-pedidos",
        },
        {
            id: 4,
            nombre: "Combos especiales",
            path: "/estudiante/combos-especiales",
        },
    ],

    
    homeStudent:[
        {
            id: 1,
            nombre: "Mis pedidos",
            path: "/estudiante/mis-pedidos",
        },
    

    ],

    
    menuStudent:[
        {
            id: 1,
            nombre: "Home",
            path: "/estudiante",
        },
        {
            id: 2,
            nombre: "Mis pedidos",
            path: "/estudiante/mis-pedidos",
        },

        {
            id: 3,
            nombre: "Combos especiales",
            path: "/estudiante/combos-especiales",
        },
    ],

    
    orderStudent:[
        {
            id: 1,
            nombre: "Home",
            path: "/estudiante",
        },
        {
            id: 2,
            nombre: "Menú del día",
            path: "/estudiante/menu-del-dia",
        },
    
        {
            id: 3,
            nombre: "Combos especiales",
            path: "/estudiante/combos-especiales",
        },
    ],

    promotionsStudent:[
        {
            id: 1,
            nombre: "Home",
            path: "/estudiante",
        },
        {
            id: 2,
            nombre: "Mis pedidos",
            path: "/estudiante/mis-pedidos",
        },
        {
            id: 3,
            nombre: "Menú del día",
            path: "/estudiante/menu-del-dia",
        },
    ],

    homeAdmin:[
    ],

    ingredientesAdmin:[
        {
            id: 1,
            nombre: "Home",
            path: "/admin",
        },
        {
            id: 2,
            nombre: "Productos",
            path: "/admin/productos",
        },
    
        {
            id: 3,
            nombre: "Combos",
            path: "/admin/combos",
        },
        {
            id: 4,
            nombre: "Promociones",
            path: "/admin/promociones",
        },
        {
            id: 5,
            nombre: "Personal de cocina",
            path: "/admin/personal-cocina",
        },
        
    ],

    productosAdmin:[
        {
            id: 1,
            nombre: "Home",
            path: "/admin",
        },
        {
            id: 2,
            nombre: "Ingredientes",
            path: "/admin/ingredientes",
        },
    
        {
            id: 3,
            nombre: "Combos",
            path: "/admin/combos",
        },
        {
            id: 4,
            nombre: "Promociones",
            path: "/admin/promociones",
        },
        {
            id: 5,
            nombre: "Personal de cocina",
            path: "/admin/personal-cocina",
        },
        
    ],

    combossAdmin:[
        {
            id: 1,
            nombre: "Home",
            path: "/admin",
        },
        {
            id: 2,
            nombre: "Productos",
            path: "/admin/productos",
        },
    
        {
            id: 3,
            nombre: "Ingredientes",
            path: "/admin/ingredientes",
        },
        {
            id: 4,
            nombre: "Promociones",
            path: "/admin/promociones",
        },
        {
            id: 5,
            nombre: "Personal de cocina",
            path: "/admin/personal-cocina",
        },
        
    ],

    promocionesAdmin:[
        {
            id: 1,
            nombre: "Home",
            path: "/admin",
        },
        {
            id: 2,
            nombre: "Productos",
            path: "/admin/productos",
        },
    
        {
            id: 3,
            nombre: "Combos",
            path: "/admin/combos",
        },
        {
            id: 4,
            nombre: "Ingredientes",
            path: "/admin/ingredientes",
        },
        {
            id: 5,
            nombre: "Personal de cocina",
            path: "/admin/personal-cocina",
        },
        
    ],
    personalAdmin:[
        {
            id: 1,
            nombre: "Home",
            path: "/admin",
        },
        {
            id: 2,
            nombre: "Productos",
            path: "/admin/productos",
        },
    
        {
            id: 3,
            nombre: "Combos",
            path: "/admin/combos",
        },
        {
            id: 4,
            nombre: "Promociones",
            path: "/admin/promociones",
        },
        {
            id: 5,
            nombre: "Ingredientes",
            path: "/admin/ingredientes",
        },
        
    ],
};