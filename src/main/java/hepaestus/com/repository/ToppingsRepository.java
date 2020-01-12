package hepaestus.com.repository;

import hepaestus.com.domain.Toppings;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Toppings entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ToppingsRepository extends JpaRepository<Toppings, Long> {

}
