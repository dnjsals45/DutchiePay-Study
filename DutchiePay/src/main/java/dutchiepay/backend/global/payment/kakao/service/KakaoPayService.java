package dutchiepay.backend.global.payment.kakao.service;

import dutchiepay.backend.domain.commerce.repository.BuyRepository;
import dutchiepay.backend.domain.order.repository.OrdersRepository;
import dutchiepay.backend.domain.order.repository.ProductRepository;
import dutchiepay.backend.entity.Buy;
import dutchiepay.backend.entity.Orders;
import dutchiepay.backend.entity.Product;
import dutchiepay.backend.entity.User;
import dutchiepay.backend.global.payment.kakao.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoPayService {

    private final ProductRepository productRepository;
    private final BuyRepository buyRepository;
    private final OrdersRepository ordersRepository;
    @Value("${host.url}")
    private String host;

    @Value("${payment.kakao.cid}")
    private String cid;

    @Value("${payment.kakao.secret}")
    private String secretKey;

    private String tid;

    @Transactional
    // 카카오페이 결제를 시작하기 위해 결제정보를 카카오페이 서버에 전달하고 결제 고유번호(TID)와 URL을 응답받는 단계
    public ReadyResponseDto kakaoPayReady(User user, ReadyRequestDto req) {
        String orderNum = "ABCDEF12345";

        Product product = productRepository.findByProductName(req.getItemName())
                .orElseThrow(() -> new IllegalArgumentException("상품명이 존재하지 않습니다."));
        Buy buy = buyRepository.findById(req.getBuyId())
                .orElseThrow(() -> new IllegalArgumentException("구매 정보가 존재하지 않습니다."));

        Orders newOrder = Orders.builder()
                .user(user)
                .product(product)
                .buy(buy)
                .address("서울시")
                .detail("어딘가")
                .totalPrice(req.getTotalAmount())
                .payment("카카오페이")
                .orderedAt(LocalDateTime.now())
                .state("주문완료")
                .amount(req.getQuantity())
                .orderNum(orderNum)
                .build();

        ordersRepository.save(newOrder);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "SECRET_KEY " + secretKey);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        KakaoPayReadyRequest body = KakaoPayReadyRequest.builder()
                .cid(cid) // 가맹점 코드(테스트용은 TC0ONETIME)
                .partnerOrderId(orderNum) // 가맹점 주문번호
                .partnerUserId(user.getNickname()) // 회원 id
                .itemName(req.getItemName()) // 상품명
                .quantity(req.getQuantity()) // 수량
                .totalAmount(req.getTotalAmount()) // 상품 총액
                .taxFreeAmount(req.getTaxFreeAmount()) // 비과세 금액
                .approvalUrl(host + "/pay/kakao/approve?orderNum=" + orderNum) // 결제 성공시 redirect url
                .cancelUrl(host + "/pay/kakao/cancel") // 결제 취소시 redirect url
                .failUrl(host + "/pay/kakao/fail") // 결제 실패시 redirect url
                .build();

        HttpEntity<KakaoPayReadyRequest> requestEntity = new HttpEntity<>(body, httpHeaders);
        ResponseEntity<ReadyResponseDto> response = new RestTemplate().postForEntity(
                "https://open-api.kakaopay.com/online/v1/payment/ready",
                requestEntity,
                ReadyResponseDto.class
        );

        ReadyResponseDto readyResponse = response.getBody();

        newOrder.readyPurchase(readyResponse.getTid());

        return readyResponse;
    }

    // 사용자가 결제 수단을 선택하고 비밀번호를 입력해 결제 인증을 완료한 뒤, 최종적으로 결제 완료 처리를 하는 단계입니다.
    // 인증완료 시 응답받은 pg_token과 tid로 최종 승인요청합니다.
    // 결제 승인 API를 호출하면 결제 준비 단계에서 시작된 결제 건이 승인으로 완료 처리됩니다.
    // 결제 승인 요청이 실패하면 카드사 등 결제 수단의 실패 정보가 필요에 따라 포함될 수 있습니다.
    public ApproveResponseDto kakaoPayApprove(String pgToken, String orderNum) {
        Orders order = ordersRepository.findByOrderNum(orderNum)
                .orElseThrow(() -> new IllegalArgumentException("주문 정보가 존재하지 않습니다."));

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

            return response.getBody();
        } catch (HttpStatusCodeException ex) {
            return null;
        }
    }

    public String makeApproveHtml(String orderNum, Integer total, String paymentStatus) {
        return String.format("""
                <html>
                <body>
                <script>
                    window.opener.postMessage({
                        type: 'PAYMENT_APPROVED',
                        payload: {
                            orderId: '%s',
                            amount: %d,
                            paymentStatus: '%s'
                        }
                    }, 'http://localhost:3000/order/success');
                    window.close();
                </script>
                </body>
                </html>
                """,
                    orderNum,
                    total,
                    paymentStatus
            );
    }
}
