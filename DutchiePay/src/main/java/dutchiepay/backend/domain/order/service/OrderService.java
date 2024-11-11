package dutchiepay.backend.domain.order.service;

import dutchiepay.backend.domain.commerce.exception.CommerceErrorCode;
import dutchiepay.backend.domain.commerce.exception.CommerceException;
import dutchiepay.backend.domain.commerce.repository.BuyRepository;
import dutchiepay.backend.domain.order.dto.CancelPurchaseRequestDto;
import dutchiepay.backend.domain.order.dto.ExchangeRequestDto;
import dutchiepay.backend.domain.order.exception.OrderErrorCode;
import dutchiepay.backend.domain.order.exception.OrderErrorException;
import dutchiepay.backend.domain.order.repository.OrderRepository;
import dutchiepay.backend.domain.order.repository.RefundRepository;
import dutchiepay.backend.entity.Buy;
import dutchiepay.backend.entity.Order;
import dutchiepay.backend.entity.Refund;
import dutchiepay.backend.entity.User;
import dutchiepay.backend.global.payment.service.KakaoPayService;
import dutchiepay.backend.global.payment.service.TossPaymentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final RefundRepository refundRepository;
    private final TossPaymentsService tossPaymentsService;
    private final KakaoPayService kakaoPayService;

    @Transactional
    public void confirmPurchase(User user, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderErrorException(OrderErrorCode.INVALID_ORDER));

        if (!order.getUser().getUserId().equals(user.getUserId())) {
            throw new OrderErrorException(OrderErrorCode.ORDER_USER_MISS_MATCH);
        }

        order.changeStatus("구매확정");
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

        if (!order.getState().equals("배송완료")) throw new OrderErrorException(OrderErrorCode.NOT_DELIVERED);

        // 환불일때만 결제 취소 진행
        if (req.getType().equals("환불")) {
            if (order.getPayment().equals("kakao")) kakaoPayService.cancelExchange(order, "환불처리");
            else if (order.getPayment().equals("card")) {
                tossPaymentsService.cancelPayment(order);
                order.changeStatus("환불처리");
            }
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
        Buy buy = order.getBuy();

        if (!order.getUser().getUserId().equals(user.getUserId())) {
            throw new OrderErrorException(OrderErrorCode.ORDER_USER_MISS_MATCH);
        }

        if (buy.getDeadline().isAfter(LocalDate.now()) && buy.getNowCount() >= buy.getSkeleton())
            throw new CommerceException(CommerceErrorCode.SUCCEEDED_BUY);

        if (order.getPayment().equals("card")) {
            if (tossPaymentsService.cancelPayment(order)) {
                order.changeStatus("취소완료");
                buy.disCount();
            }
        } else if (order.getPayment().equals("kakao")) {
            if (kakaoPayService.cancelExchange(order.getOrderNum(), "취소완료")) {
                buy.disCount();
            } else {
                throw new OrderErrorException(OrderErrorCode.KAKAOPAY_CANCEL_FAILED);
            }
        } else {
            throw new OrderErrorException(OrderErrorCode.INVALID_PAYMENT);
        }
    }
}
