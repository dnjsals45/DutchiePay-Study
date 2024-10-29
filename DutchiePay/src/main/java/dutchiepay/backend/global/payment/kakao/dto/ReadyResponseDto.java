package dutchiepay.backend.global.payment.kakao.dto;

import lombok.*;
import java.util.Date;

@Getter
@AllArgsConstructor
public class ReadyResponseDto {
    private String tid;
    private String next_redirect_pc_url;
    private Date created_at;
}
