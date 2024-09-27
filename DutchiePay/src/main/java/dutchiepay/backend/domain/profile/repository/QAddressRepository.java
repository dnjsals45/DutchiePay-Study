package dutchiepay.backend.domain.profile.repository;

import dutchiepay.backend.entity.Address;
import dutchiepay.backend.entity.User;

import java.util.List;

public interface QAddressRepository {
    List<Address> findAllByUser(User user);

    void changeOldestAddressToDefault(User user);

    void changeIsDefaultTrueToFalse(User user);
}
