package dutchiepay.backend.domain.order.service;

import dutchiepay.backend.domain.order.dto.CancelPurchaseRequestDto;
import dutchiepay.backend.domain.order.dto.ExchangeRequestDto;
import dutchiepay.backend.domain.order.exception.OrdersErrorCode;
import dutchiepay.backend.domain.order.exception.OrdersErrorException;
import dutchiepay.backend.domain.order.repository.OrdersRepository;
import dutchiepay.backend.domain.order.repository.RefundRepository;
import dutchiepay.backend.entity.Orders;
import dutchiepay.backend.entity.Refund;
import dutchiepay.backend.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrdersService {
    private final OrdersRepository ordersRepository;
    private final RefundRepository refundRepository;

    @Transactional
    public void confirmPurchase(User user, Long orderId) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new OrdersErrorException(OrdersErrorCode.INVALID_ORDER));

        if (order.getUser() != user) {
            throw new OrdersErrorException(OrdersErrorCode.ORDER_USER_MISS_MATCH);
        }

        order.confirmPurchase();
    }

    @Transactional
    public void applyExchange(User user, ExchangeRequestDto req) {
        Orders order = ordersRepository.findById(req.getOrderId())
                .orElseThrow(() -> new OrdersErrorException(OrdersErrorCode.INVALID_ORDER));

        if (order.getUser() != user) {
            throw new OrdersErrorException(OrdersErrorCode.ORDER_USER_MISS_MATCH);
        }

        if (!req.getType().equals("교환") && !req.getType().equals("환불")) {
            throw new OrdersErrorException(OrdersErrorCode.INVALID_EXCHANGE_TYPE);
        }

        Refund newRefund = Refund.builder()
                .user(user)
                .store(order.getProduct().getStore())
                .orders(order)
                .category(req.getType())
                .reason(req.getReason())
                .detail(req.getDetail())
                .build();

        refundRepository.save(newRefund);
    }

    @Transactional
    public void cancelPurchase(User user, CancelPurchaseRequestDto req) {
        Orders order = ordersRepository.findById(req.getOrderId())
                .orElseThrow(() -> new OrdersErrorException(OrdersErrorCode.INVALID_ORDER));

        if (order.getUser() != user) {
            throw new OrdersErrorException(OrdersErrorCode.ORDER_USER_MISS_MATCH);
        }

        order.cancelPurchase();
    }
}
