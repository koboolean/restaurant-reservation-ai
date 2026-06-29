package koboolean.multiai.repo;

import koboolean.multiai.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {


    Optional<Customer> findByPhoneNumber(String phoneNumber);
}
