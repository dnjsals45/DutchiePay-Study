package dutchiepay.backend.domain.order.service;

import dutchiepay.backend.domain.order.dto.CancelPurchaseRequestDto;
import dutchiepay.backend.domain.order.dto.ExchangeRequestDto;
import dutchiepay.backend.domain.order.exception.OrderErrorCode;
import dutchiepay.backend.domain.order.exception.OrderErrorException;
import dutchiepay.backend.domain.order.repository.OrderRepository;
import dutchiepay.backend.domain.order.repository.RefundRepository;
import dutchiepay.backend.entity.Order;
import dutchiepay.backend.entity.Refund;
import dutchiepay.backend.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final RefundRepository refundRepository;

    @Transactional
    public void confirmPurchase(User user, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderErrorException(OrderErrorCode.INVALID_ORDER));

        if (!order.getUser().getUserId().equals(user.getUserId())) {
            throw new OrderErrorException(OrderErrorCode.ORDER_USER_MISS_MATCH);
        }

        order.confirmPurchase();
    }

    @Transactional
    public void applyExchange(User user, ExchangeRequestDto req) {
        Order order = orderRepository.findById(req.getOrderId())
                .orElseThrow(() -> new OrderErrorException(OrderErrorCode.INVALID_ORDER));

        if (!order.getUser().getUserId().equals(user.getUserId())) {
            throw new OrderErrorException(OrderErrorCode.ORDER_USER_MISS_MATCH);
        }

        if (!req.getType().equals("교환") && !req.getType().equals("환불")) {
            throw new OrderErrorException(OrderErrorCode.INVALID_EXCHANGE_TYPE);
        }

        Refund newRefund = Refund.builder()
                .user(user)
                .store(order.getProduct().getStore())
                .order(order)
                .category(req.getType())
                .reason(req.getReason())
                .detail(req.getDetail())
                .build();

        refundRepository.save(newRefund);
    }

    @Transactional
    public void cancelPurchase(User user, CancelPurchaseRequestDto req) {
        Order order = orderRepository.findById(req.getOrderId())
                .orElseThrow(() -> new OrderErrorException(OrderErrorCode.INVALID_ORDER));

        if (!order.getUser().getUserId().equals(user.getUserId())) {
            throw new OrderErrorException(OrderErrorCode.ORDER_USER_MISS_MATCH);
        }

        order.cancelPurchase();
    }
}
