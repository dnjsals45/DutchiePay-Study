package dutchiepay.backend.domain.profile.repository;

import dutchiepay.backend.entity.Address;
import dutchiepay.backend.entity.User;
import dutchiepay.backend.entity.UsersAddress;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UsersAddressRepository extends JpaRepository<UsersAddress, Long> {
    void deleteByUserAndAddress(User user, Address address);
}
