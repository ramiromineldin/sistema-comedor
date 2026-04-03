package ar.uba.fi.ingsoft1.product_example.promotions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    @Query("select p from Promotion p where p.active = true and p.deleted = false and p.endDate >= :now")
    List<Promotion> findActiveNow(@Param("now") LocalDateTime now);

}