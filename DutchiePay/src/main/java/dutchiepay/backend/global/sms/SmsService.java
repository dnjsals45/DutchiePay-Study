package dutchiepay.backend.global.sms;

import dutchiepay.backend.domain.user.dto.SmsAuthResponseDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

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

    public SmsAuthResponseDto sendVerificationMessage(String to) {
        Message message = new Message();
        message.setFrom(sender);
        message.setTo(to);

        String verificationCode = generateVerificationCode();

        message.setText("[더취페이] 인증번호는 " + verificationCode + "입니다.");

        messageService.sendOne(new SingleMessageSendingRequest(message));

        return SmsAuthResponseDto.of(verificationCode);
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int number = random.nextInt(10000);
        return String.format("%04d", number);
    }
}
