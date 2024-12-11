package dutchiepay.backend.global.payment.service;

import dutchiepay.backend.domain.order.exception.OrderErrorCode;
import dutchiepay.backend.domain.order.exception.OrderErrorException;
import dutchiepay.backend.domain.order.repository.OrderRepository;
import dutchiepay.backend.entity.Order;
import dutchiepay.backend.entity.User;
import dutchiepay.backend.global.payment.dto.kakao.*;
import dutchiepay.backend.global.payment.exception.PaymentErrorCode;
import dutchiepay.backend.global.payment.exception.PaymentErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoPayService {

    private final OrderRepository ordersRepository;
    private final KakaoPayRequestService kakaoPayRequestService;

    @Value("${host.frontend}")
    private String frontendHost;

    public KakaoPayReadyResponseDto kakaoPayReady(User user, ReadyRequestDto dto) {
        return kakaoPayRequestService.ready(user, dto);
    }

    @Transactional
    public ApproveResponseDto kakaoPayApprove(String pgToken, String orderNum) {
        Order order = ordersRepository.findByOrderNum(orderNum)
                .orElseThrow(() -> new OrderErrorException(OrderErrorCode.INVALID_ORDER));

        ApproveResponseDto response = kakaoPayRequestService.approve(pgToken, order);

        if (response == null) {
            throw new PaymentErrorException(PaymentErrorCode.INVALID_KAKAO_APPROVE_RESPONSE);
        }

        order.changeStatus("공구진행중");
        order.getBuy().upCount(order.getQuantity());

        return response;
    }

    @Transactional
    public boolean kakaoPayCancel(Order order, String state) {
        if (!kakaoPayRequestService.cancel(order)) {
            throw new PaymentErrorException(PaymentErrorCode.ERROR_KAKAOPAY_CANCEL);
        }

        order.changeStatus(state);
        return true;
    }

    @Transactional
    public boolean kakaoPayCancel(String orderNum, String state) {
        Order order = ordersRepository.findByOrderNum(orderNum)
                .orElseThrow(() -> new OrderErrorException(OrderErrorCode.INVALID_ORDER));

        if (!kakaoPayRequestService.cancel(order)) {
            throw new PaymentErrorException(PaymentErrorCode.ERROR_KAKAOPAY_CANCEL);
        }

        order.changeStatus(state);
        return true;
    }

    public String kakaoPayCheckStatus(Order order) {
        String status = kakaoPayRequestService.checkStatus(order);

        if (status == null) {
            throw new PaymentErrorException(PaymentErrorCode.ERROR_KAKAOPAY_STATUS);
        }

        return status;
    }

    public String kakaoPayCheckStatus(String orderNum) {
        Order order = ordersRepository.findByOrderNum(orderNum)
                .orElseThrow(() -> new OrderErrorException(OrderErrorCode.INVALID_ORDER));

        String status = kakaoPayRequestService.checkStatus(order);

        if (status == null) {
            throw new PaymentErrorException(PaymentErrorCode.ERROR_KAKAOPAY_STATUS);
        }

        return status;
    }

    @Transactional
    public void kakaoPayFail(String orderNum) {
        Order order = ordersRepository.findByOrderNum(orderNum)
                .orElseThrow(() -> new OrderErrorException(OrderErrorCode.INVALID_ORDER));

        order.changeStatus("결제실패");
    }

    public String makePostMessage(String orderNum, String paymentStatus) {
        return String.format("""
                <html>
                <body>
                <script>
                    window.opener.postMessage({
                        type: '%s',
                        orderNum: '%s',
                    }, '%s/order');
                    window.close();
                </script>
                </body>
                </html>
                """,
                    paymentStatus,
                    orderNum,
                    frontendHost
            );
    }
}
