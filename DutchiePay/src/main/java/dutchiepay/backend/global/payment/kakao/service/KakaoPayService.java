package dutchiepay.backend.global.payment.kakao.service;

import dutchiepay.backend.global.payment.kakao.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoPayService {

    @Value("${host.url}")
    private String host;

    @Value("${payment.kakao.cid}")
    private String cid;

    @Value("${payment.kakao.secret}")
    private String secretKey;

    private String tid;

    // 카카오페이 결제를 시작하기 위해 결제정보를 카카오페이 서버에 전달하고 결제 고유번호(TID)와 URL을 응답받는 단계
    public ReadyResponseDto kakaoPayReady(ReadyRequestDto req) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "SECRET_KEY " + secretKey);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        KakaoPayReadyRequest body = KakaoPayReadyRequest.builder()
                .cid(cid) // 가맹점 코드(테스트용은 TC0ONETIME)
                .partnerOrderId(req.getPartnerOrderId()) // 가맹점 주문번호
                .partnerUserId(req.getPartnerUserId()) // 회원 id
                .itemName(req.getItemName()) // 상품명
                .quantity(req.getQuantity()) // 수량
                .totalAmount(req.getTotalAmount()) // 상품 총액
                .taxFreeAmount(req.getTaxFreeAmount()) // 비과세 금액
                .approvalUrl(host + "/pay/kakao/approve") // 결제 성공시 redirect url
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
        return readyResponse;
    }

    // 사용자가 결제 수단을 선택하고 비밀번호를 입력해 결제 인증을 완료한 뒤, 최종적으로 결제 완료 처리를 하는 단계입니다.
    // 인증완료 시 응답받은 pg_token과 tid로 최종 승인요청합니다.
    // 결제 승인 API를 호출하면 결제 준비 단계에서 시작된 결제 건이 승인으로 완료 처리됩니다.
    // 결제 승인 요청이 실패하면 카드사 등 결제 수단의 실패 정보가 필요에 따라 포함될 수 있습니다.
    public ApproveResponseDto kakaoPayApprove(String pgToken, ApproveRequestDto req) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "SECRET_KEY " + secretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        KakaoPayApproveRequest approveRequest = KakaoPayApproveRequest.builder()
                .cid(cid)
                .tid(req.getTid())
                .partnerOrderId(req.getPartnerOrderId())
                .partnerUserId(req.getPartnerUserId())
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
}
