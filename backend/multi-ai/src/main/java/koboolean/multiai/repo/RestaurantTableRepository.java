package koboolean.multiai.repo;

import koboolean.multiai.domain.TableType;
import koboolean.multiai.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {
    List<RestaurantTable> findByCapacityGreaterThanEqualAndType(Integer capacityIsGreaterThan, TableType type);

    List<RestaurantTable> findByCapacityGreaterThanEqual(Integer capacityIsGreaterThan);
}
