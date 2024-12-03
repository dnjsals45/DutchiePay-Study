package dutchiepay.backend.domain.order.service;

import dutchiepay.backend.domain.order.exception.OrderErrorCode;
import dutchiepay.backend.domain.order.exception.OrderErrorException;
import dutchiepay.backend.domain.order.repository.OrderRepository;
import dutchiepay.backend.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderUtilService {
    private final OrderRepository orderRepository;

    public Order findById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderErrorException(OrderErrorCode.INVALID_ORDER));
    }
}
