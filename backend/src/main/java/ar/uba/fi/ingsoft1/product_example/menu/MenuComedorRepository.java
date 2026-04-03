package ar.uba.fi.ingsoft1.product_example.menu;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MenuComedorRepository extends JpaRepository<MenuComedor, Long> {
    List<MenuComedor> findByActivoTrue();
    Optional<MenuComedor> findFirstByActivoTrueAndFechaOrderByIdAsc(LocalDate fecha);
}
