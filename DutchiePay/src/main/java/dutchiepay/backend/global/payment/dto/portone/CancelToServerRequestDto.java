package dutchiepay.backend.global.payment.dto.portone;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class CancelToServerRequestDto {

    private String reason;

}
