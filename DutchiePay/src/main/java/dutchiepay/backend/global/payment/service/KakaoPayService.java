package dutchiepay.backend.global.payment.service;

import dutchiepay.backend.domain.commerce.repository.BuyRepository;
import dutchiepay.backend.domain.order.exception.OrderErrorCode;
import dutchiepay.backend.domain.order.exception.OrderErrorException;
import dutchiepay.backend.domain.order.repository.OrderRepository;
import dutchiepay.backend.domain.order.repository.ProductRepository;
import dutchiepay.backend.entity.Buy;
import dutchiepay.backend.entity.Order;
import dutchiepay.backend.entity.Product;
import dutchiepay.backend.entity.User;
import dutchiepay.backend.global.payment.dto.kakao.*;
import dutchiepay.backend.global.payment.exception.PaymentErrorCode;
import dutchiepay.backend.global.payment.exception.PaymentErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoPayService {

    private final ProductRepository productRepository;
    private final BuyRepository buyRepository;
    private final OrderRepository ordersRepository;
    @Value("${host.backend}")
    private String backendHost;

    @Value("${host.frontend}")
    private String frontendHost;

    @Value("${payment.kakao.cid}")
    private String cid;

    @Value("${payment.kakao.secret}")
    private String secretKey;

    // 카카오페이 결제를 시작하기 위해 결제정보를 카카오페이 서버에 전달하고 결제 고유번호(TID)와 URL을 응답받는 단계
    @Transactional
    public KakaoPayReadyResponseDto kakaoPayReady(User user, ReadyRequestDto req) {
        Product product = productRepository.findByProductName(req.getProductName())
                .orElseThrow(() -> new PaymentErrorException(PaymentErrorCode.INVALID_PRODUCT));
        Buy buy = buyRepository.findByProduct(product)
                .orElseThrow(() -> new PaymentErrorException(PaymentErrorCode.INVALID_BUY));

        Order newOrder = Order.builder()
                .user(user)
                .product(product)
                .buy(buy)
                .receiver(req.getReceiver())
                .zipCode(req.getZipCode())
                .address(req.getAddress())
                .detail(req.getDetail())
                .message(req.getMessage())
                .totalPrice(req.getTotalAmount())
                .payment("카카오페이")
                .orderedAt(LocalDateTime.now())
                .state("주문완료")
                .amount(req.getQuantity())
                .orderNum(generateOrderNumber())
                .build();

        ordersRepository.save(newOrder);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "SECRET_KEY " + secretKey);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        KakaoPayReadyRequest body = KakaoPayReadyRequest.builder()
                .cid(cid) // 가맹점 코드(테스트용은 TC0ONETIME)
                .partnerOrderId(newOrder.getOrderNum()) // 가맹점 주문번호
                .partnerUserId(user.getNickname()) // 회원 id
                .itemName(req.getProductName()) // 상품명
                .quantity(req.getQuantity()) // 수량
                .totalAmount(req.getTotalAmount()) // 상품 총액
                .taxFreeAmount(req.getTaxFreeAmount()) // 비과세 금액
                .approvalUrl(backendHost + "/pay/kakao/approve?orderNum=" + newOrder.getOrderNum()) // 결제 성공시 redirect url
                .cancelUrl(backendHost + "/pay/kakao/cancel?orderNum=" + newOrder.getOrderNum()) // 결제 취소시 redirect url
                .failUrl(backendHost + "/pay/kakao/fail?orderNum=" + newOrder.getOrderNum()) // 결제 실패시 redirect url
                .build();

        HttpEntity<KakaoPayReadyRequest> requestEntity = new HttpEntity<>(body, httpHeaders);
        ResponseEntity<ReadyResponseDto> response = new RestTemplate().postForEntity(
                "https://open-api.kakaopay.com/online/v1/payment/ready",
                requestEntity,
                ReadyResponseDto.class
        );

        ReadyResponseDto readyResponse = response.getBody();

        newOrder.readyPurchase(readyResponse.getTid());

        return KakaoPayReadyResponseDto.from(readyResponse.getNext_redirect_pc_url());
    }

    // 사용자가 결제 수단을 선택하고 비밀번호를 입력해 결제 인증을 완료한 뒤, 최종적으로 결제 완료 처리를 하는 단계입니다.
    // 인증완료 시 응답받은 pg_token과 tid로 최종 승인요청합니다.
    // 결제 승인 API를 호출하면 결제 준비 단계에서 시작된 결제 건이 승인으로 완료 처리됩니다.
    // 결제 승인 요청이 실패하면 카드사 등 결제 수단의 실패 정보가 필요에 따라 포함될 수 있습니다.
    @Transactional
    public ApproveResponseDto kakaoPayApprove(String pgToken, String orderNum) {
        Order order = ordersRepository.findByOrderNum(orderNum)
                .orElseThrow(() -> new OrderErrorException(OrderErrorCode.INVALID_ORDER));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "SECRET_KEY " + secretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        KakaoPayApproveRequest approveRequest = KakaoPayApproveRequest.builder()
                .cid(cid)
                .tid(order.getTid())
                .partnerOrderId(orderNum)
                .partnerUserId(order.getUser().getNickname())
                .pgToken(pgToken)
                .build();

        HttpEntity<KakaoPayApproveRequest> entityMap = new HttpEntity<>(approveRequest, headers);
        try {
            ResponseEntity<ApproveResponseDto> response = new RestTemplate().postForEntity(
                    "https://open-api.kakaopay.com/online/v1/payment/approve",
                    entityMap,
                    ApproveResponseDto.class
            );

            order.approvePayment();

            return response.getBody();
        } catch (HttpStatusCodeException ex) {
            return null;
        }
    }

    @Transactional
    public void kakaoPayCancel(String orderNum) {
        Order order = ordersRepository.findByOrderNum(orderNum)
                .orElseThrow(() -> new OrderErrorException(OrderErrorCode.INVALID_ORDER));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "SECRET_KEY " + secretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        KakaoPayCancelRequest cancelRequest = KakaoPayCancelRequest.builder()
                .cid(cid)
                .tid(order.getTid())
                .cancelAmount(String.valueOf(order.getTotalPrice()))
                .cancelTaxFreeAmount("0")
                .build();

        HttpEntity<KakaoPayCancelRequest> entityMap = new HttpEntity<>(cancelRequest, headers);

        try {
            ResponseEntity<CancelResponseDto> response = new RestTemplate().postForEntity(
                    "https://open-api.kakaopay.com/online/v1/payment/cancel",
                    entityMap,
                    CancelResponseDto.class
            );

            order.cancelPayment();
            log.info("response: {}", response.getBody());
        } catch (HttpStatusCodeException ex) {
            log.error("카카오페이 결제 취소 실패: Status code: {}, Response body: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
        }
    }

    public String kakaPayCheckStatus(String orderNum) {
        Order order = ordersRepository.findByOrderNum(orderNum)
                .orElseThrow(() -> new OrderErrorException(OrderErrorCode.INVALID_ORDER));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "SECRET_KEY " + secretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        KakaoPayCheckStatusRequest request = KakaoPayCheckStatusRequest.builder()
                .cid(cid)
                .tid(order.getTid())
                .build();

        HttpEntity<KakaoPayCheckStatusRequest> entityMap = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<KakaoPayCheckStatusResponse> response = new RestTemplate().postForEntity(
                    "https://open-api.kakaopay.com/online/v1/payment/order",
                    entityMap,
                    KakaoPayCheckStatusResponse.class
            );

            return response.getBody().getStatus();
        } catch (HttpStatusCodeException ex) {
            log.error("카카오페이 결제 상태 조회 실패");
        }

        return null;
    }

    public String makeApproveHtml(String orderNum, String paymentStatus) {
        return String.format("""
                <html>
                <body>
                <script>
                    window.opener.postMessage({
                        type: '%s',
                        orderId: '%s',
                    }, '%s/order');
                    window.close();
                </script>
                </body>
                </html>
                """,
                    frontendHost,
                    paymentStatus,
                    orderNum
            );
    }

    public String makeCancelHtml(String orderNum, String paymentStatus) {
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
                    frontendHost,
                    paymentStatus,
                    orderNum
            );
    }

    public String makeFailHtml(String orderNum, String paymentStatus) {
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
                    frontendHost,
                    paymentStatus,
                    orderNum
            );
    }

    private String generateOrderNumber() {
        Random random = new Random();
        String orderNum;
        do {
            String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
            int randomNum = 10000 + random.nextInt(90000);
            orderNum = dateStr + randomNum;
        } while (ordersRepository.existsByOrderNum(orderNum)); // 중복 체크

        return orderNum;
    }


    public void kakaoPayFail(String orderNum) {
        Order order = ordersRepository.findByOrderNum(orderNum)
                .orElseThrow(() -> new OrderErrorException(OrderErrorCode.INVALID_ORDER));

        order.failPayment();
    }
}
