package dutchiepay.backend.global.sms;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsService {
    @Value("${spring.sms.api-key}")
    private String apiKey;

    @Value("${spring.sms.api-secret}")
    private String apiSecret;

    @Value("${spring.sms.provider}")
    private String provider;

    @Value("${spring.sms.sender}")
    private String sender;

    private DefaultMessageService messageService;

    @PostConstruct
    public void init() {
        messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, provider);
    }

    public String sendVerificationMessage(String to) {
        Message message = new Message();
        message.setFrom(sender);
        message.setTo(to);
        message.setText("인증번호 전송 테스트");

        // TODO 인증번호 저장해두고 나중에 검증하는 api 추가 필요
        messageService.sendOne(new SingleMessageSendingRequest(message));

        return "1234";
    }

    private String generateVerificationCode() {
        return "1234";
    }
}
