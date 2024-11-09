package dutchiepay.backend.global.payment.service;

import dutchiepay.backend.domain.commerce.exception.CommerceErrorCode;
import dutchiepay.backend.domain.commerce.exception.CommerceException;
import dutchiepay.backend.domain.commerce.repository.BuyRepository;
import dutchiepay.backend.domain.order.exception.OrderErrorCode;
import dutchiepay.backend.domain.order.exception.OrderErrorException;
import dutchiepay.backend.domain.order.repository.OrderRepository;
import dutchiepay.backend.domain.order.repository.ProductRepository;
import dutchiepay.backend.entity.Buy;
import dutchiepay.backend.entity.Order;
import dutchiepay.backend.entity.Product;
import dutchiepay.backend.entity.User;
import dutchiepay.backend.global.payment.dto.portone.CancelToServerRequestDto;
import dutchiepay.backend.global.payment.dto.portone.TossPaymentsCancelResponseDto;
import dutchiepay.backend.global.payment.dto.portone.TossPaymentsValidateRequestDto;
import dutchiepay.backend.global.payment.dto.portone.TossPaymentsValidatePaidResponseDto;
import dutchiepay.backend.global.payment.exception.PaymentErrorCode;
import dutchiepay.backend.global.payment.exception.PaymentErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;


@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TossPaymentsService {

    @Value("${PORTONE_API_SECRET}")
    private String apiSecret;

    @Value("${PORTONE_STORE_ID}")
    private String storeId;

    private final OrderRepository orderRepository;
    private final BuyRepository buyRepository;

    /**
     * portone 단건 조회 api 호출
     * @param user 현재 로그인한 사용자
     * @param validateRequestDto 결제 정보를 담은 Dto
     */
    public void validateResult(User user, TossPaymentsValidateRequestDto validateRequestDto) {

        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", "PortOne " + apiSecret);
        HttpEntity<Void> httpEntity = new HttpEntity<>(header);

        ResponseEntity<TossPaymentsValidatePaidResponseDto> response = null;
        try {
            response = new RestTemplate().exchange(
                    "https://api.portone.io/payments/" + validateRequestDto.getPaymentId(),
                    HttpMethod.GET,
                    httpEntity,
                    TossPaymentsValidatePaidResponseDto.class
            );
            // 검증에 성공하면 order 엔티티 생성 후 저장
            if (validatePayment(response.getBody(), validateRequestDto)) {
                makeOrder(validateRequestDto, user);

            }
        } catch (HttpStatusCodeException e) {
            log.error("결제 정보 불러오기 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 포트원에서 받은 결제 결과를 검증하는 메서드
     * @param response 결제 결과를 담은 Dto
     * @param request 프론트에서 받은 결제 정보
     * @return 성공 - True, 실패 - exception
     */
    private boolean validatePayment(TossPaymentsValidatePaidResponseDto response, TossPaymentsValidateRequestDto request) {
        // 검증 목록: 상태, 통화, 결제 총액, 결제 ID, 상품 이름, 실 결제액, 포트원 storeId
        if (!response.getStatus().equals("PAID") || !response.getCurrency().equals("KRW") ||
                !request.getTotalAmount().equals(response.getAmount().getTotal()) ||
                !request.getPaymentId().equals(response.getId()) ||
                !request.getProductName().equals(response.getOrderName()) ||
                !request.getTotalAmount().equals(response.getAmount().getPaid()) ||
                !response.getStoreId().equals(storeId) || !response.getChannel().getPgProvider().equals("TOSSPAYMENTS")
        ) throw new PaymentErrorException(PaymentErrorCode.INVALID_BUY);

        // 이미 주문 정보가 존재하거나 취소, 실패한 결제이면 exception 반환
        if (orderRepository.findByTid(response.getId()) != null ||
                response.getStatus().equals("CANCELLED") || response.getStatus().equals("FAIL"))
            throw new PaymentErrorException(PaymentErrorCode.FINISHED_PAYMENT);

        return true;
    }

    /**
     * Order 엔티티를 생성하고 저장하는 메서드
     * buyId로 Buy 객체를 찾고, 조건에 따라 Exception 발생
     * order 객체 생성 후 공구 기한 확인 ->
     * 진행중 : 공구진행중, nowCount 증가
     * 마감 : 배송준비중 / 공구실패 + nowCount 감소
     * @param requestDto 주문 정보가 담겨있는 Dto
     * @param user 현재 로그인한 사용자
     */
    @Transactional
    protected void makeOrder(TossPaymentsValidateRequestDto requestDto, User user) {
        Buy buy = buyRepository.findById(requestDto.getBuyId())
                .orElseThrow(() -> new CommerceException(CommerceErrorCode.CANNOT_FOUND_BUY));
        if (buy.getDeadline().isBefore(LocalDate.now())) throw new CommerceException(CommerceErrorCode.AFTER_DUE_DATE);
        Order newOrder = orderRepository.save(Order.builder()
                .tid(requestDto.getPaymentId())
                .quantity(Integer.parseInt(requestDto.getQuantity().toString()))
                .address(requestDto.getAddress())
                .detail(requestDto.getDetail())
                .zipCode(requestDto.getZipCode())
                .message(requestDto.getMessage())
                .orderNum(generateOrderNumber())
                .receiver(requestDto.getReceiver())
                .orderedAt(LocalDateTime.now())
                .user(user)
                .product(buy.getProduct())
                .buy(buy)
                .phone(requestDto.getPhone())
                .payment("card")
                .totalPrice(Integer.parseInt(requestDto.getTotalAmount().toString()))
                .state("주문완료")
                .build());
        // 공동구매 기한 확인
        if (buy.getDeadline().isBefore(LocalDate.now())) {
            buy.upCount();
            newOrder.changeStatus("공구진행중");
        }
        else if (buy.getDeadline().isAfter(LocalDate.now())) {
            // 최소 인원 충족
            if (buy.getNowCount() >= buy.getSkeleton()) newOrder.changeStatus("배송진행중");
            else {
                newOrder.changeStatus("공구실패");
                // 결제 취소
                this.cancelPayment(newOrder);
            }
        }
    }

    private String generateOrderNumber() {
        Random random = new Random();
        String orderNum;
        do {
            String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
            random.setSeed(System.currentTimeMillis());
            int randomNum = 10000 + random.nextInt(90000);
            orderNum = dateStr + randomNum;
        } while (orderRepository.existsByOrderNum(orderNum)); // 중복 체크

        return orderNum;
    }

    public boolean cancelPayment(Order order) {
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", "PortOne " + apiSecret);
        CancelToServerRequestDto cancel = CancelToServerRequestDto.builder().reason("reason").build();
        HttpEntity<CancelToServerRequestDto> httpEntity = new HttpEntity<>(cancel, header);

        ResponseEntity<TossPaymentsCancelResponseDto> response = null;
        try {
            response = new RestTemplate().exchange(
                    "https://api.portone.io/payments/" + order.getTid() + "/cancel",
                    HttpMethod.POST,
                    httpEntity,
                    TossPaymentsCancelResponseDto.class
            );
            if (response.getBody().getStatus().equals("SUCCEEDED") &&
                    response.getBody().getTotalAmount().equals(BigDecimal.valueOf(order.getTotalPrice()))) {
                return true;
            }
        } catch (HttpStatusCodeException e) {
            log.error("결제 취소 중 오류 발생: " + e.getMessage());
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST)
                throw new PaymentErrorException(PaymentErrorCode.INVALID_REQUEST);
        }
        return false;
    }

}
