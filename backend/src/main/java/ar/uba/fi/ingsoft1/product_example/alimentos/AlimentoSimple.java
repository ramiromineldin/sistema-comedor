package ar.uba.fi.ingsoft1.product_example.alimentos;

public interface AlimentoSimple {
    
    boolean hasStock();
    
    void incrementStock(Integer quantity);
    
    void decrementStock(Integer quantity);
    
    void setStock(Integer stock);
}