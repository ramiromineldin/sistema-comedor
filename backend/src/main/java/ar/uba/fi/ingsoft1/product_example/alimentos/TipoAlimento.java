package ar.uba.fi.ingsoft1.product_example.alimentos;

public enum TipoAlimento {
    INGREDIENT("Ingrediente"),
    PRODUCT("Producto"), 
    COMBO("Combo");
    
    private final String descripcion;
    
    TipoAlimento(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    @Override
    public String toString() {
        return descripcion;
    }
}