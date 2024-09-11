package dutchiepay.backend.domain.order.repository;

import dutchiepay.backend.entity.User;

public interface QOrderRepository {
    Long countByUserPurchase(User user, String state);
}
