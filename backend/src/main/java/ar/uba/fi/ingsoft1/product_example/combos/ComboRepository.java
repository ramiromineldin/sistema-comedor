package ar.uba.fi.ingsoft1.product_example.combos;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComboRepository extends JpaRepository<Combo, Long> {
    List<Combo> findByNameContaining(String namePart);
}
