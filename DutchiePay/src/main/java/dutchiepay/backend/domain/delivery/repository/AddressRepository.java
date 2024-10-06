package dutchiepay.backend.domain.delivery.repository;

import dutchiepay.backend.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long>, QAddressRepository {
}
