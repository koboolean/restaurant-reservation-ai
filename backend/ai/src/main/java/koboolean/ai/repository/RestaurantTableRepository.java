package koboolean.ai.repository;

import koboolean.ai.domain.TableType;
import koboolean.ai.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {

    List<RestaurantTable> findByCapacityGreaterThanEqual(Integer capacity);

    List<RestaurantTable> findByCapacityGreaterThanEqualAndType(Integer capacity, TableType type);

}
