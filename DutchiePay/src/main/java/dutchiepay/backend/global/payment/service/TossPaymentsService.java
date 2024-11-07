package dutchiepay.backend.global.payment.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dutchiepay.backend.domain.commerce.exception.CommerceErrorCode;
import dutchiepay.backend.domain.commerce.exception.CommerceException;
import dutchiepay.backend.domain.commerce.repository.BuyRepository;
import dutchiepay.backend.domain.order.repository.OrderRepository;
import dutchiepay.backend.domain.order.repository.ProductRepository;
import dutchiepay.backend.entity.Order;
import dutchiepay.backend.entity.Product;
import dutchiepay.backend.entity.User;
import dutchiepay.backend.global.payment.dto.portone.TossValidateRequestDto;
import dutchiepay.backend.global.payment.dto.portone.responsedto.TossValidatePaidResponseDto;
import dutchiepay.backend.global.payment.exception.PaymentErrorCode;
import dutchiepay.backend.global.payment.exception.PaymentErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

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
    private final ProductRepository productRepository;
    private final BuyRepository buyRepository;

    public void validateResult(User user, TossValidateRequestDto validateRequestDto) {

        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", "PortOne " + apiSecret);
        HttpEntity<Void> httpEntity = new HttpEntity<>(header);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        ResponseEntity<TossValidatePaidResponseDto> response = null;
        try {
            response = new RestTemplate().exchange(
                    "https://api.portone.io/payments/" + validateRequestDto.getPaymentId(),
                    HttpMethod.GET,
                    httpEntity,
                    TossValidatePaidResponseDto.class
            );
            if (validatePayment(response.getBody(), validateRequestDto)) {
                makeOrder(validateRequestDto, user);
            }
        } catch (HttpStatusCodeException e) {
            log.error("결제 정보 불러오기 중 오류 발생: " + e.getMessage());
        }
    }

    private boolean validatePayment(TossValidatePaidResponseDto response, TossValidateRequestDto request) {
        if (!response.getStatus().equals("PAID") || !response.getCurrency().equals("KRW") ||
                !request.getTotalAmount().equals(response.getAmount().getTotal()) ||
                !request.getPaymentId().equals(response.getId()) ||
                !request.getProductName().equals(response.getOrderName()) ||
                !request.getTotalAmount().equals(response.getAmount().getPaid()) ||
                !response.getStoreId().equals(storeId) || !response.getChannel().getPgProvider().equals("TOSSPAYMENTS")
        ) throw new PaymentErrorException(PaymentErrorCode.INVALID_BUY);
        if (orderRepository.findByTid(response.getId()) != null)
            throw new PaymentErrorException(PaymentErrorCode.FINISHED_PAYMENT);
        return true;
    }

    private void makeOrder(TossValidateRequestDto requestDto, User user) {
        Product product = productRepository.findByProductName(requestDto.getProductName())
                .orElseThrow(() -> new CommerceException(CommerceErrorCode.CANNOT_FOUND_PRODUCT));
        orderRepository.save(Order.builder()
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
                .product(product)
                .buy(buyRepository.findByProduct(product).orElseThrow(() -> new CommerceException(CommerceErrorCode.CANNOT_FOUND_BUY)))
                .phone(requestDto.getPhone())
                .payment("토스페이먼츠")
                .totalPrice(Integer.parseInt(requestDto.getTotalAmount().toString()))
                .state("주문완료")
                .build());
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
}
