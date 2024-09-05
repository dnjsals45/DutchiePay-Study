package dutchiepay.backend.domain.profile.dto;

import dutchiepay.backend.entity.Ask;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetMyAskResponseDto {
    private Long askId;
    private String storeName;
    private String orderNum;
    private Long productId;
    private String content;
    private String answer;
    private LocalDateTime answeredAt;
    private Boolean isSecret;

    public static List<GetMyAskResponseDto> from(List<Ask> asks) {
        List<GetMyAskResponseDto> response = new ArrayList<>();

        for (Ask ask : asks) {
            response.add(GetMyAskResponseDto.builder()
                    .askId(ask.getAskId())
                    .storeName(ask.getProduct().getStoreId().getStoreName())
                    .orderNum(ask.getOrderNum())
                    .productId(ask.getProduct().getProductId())
                    .content(ask.getContents())
                    .answer(ask.getAnswer())
                    .answeredAt(ask.getAnsweredAt())
                    .isSecret(ask.isSecret())
                    .build());
        }

        return response;
    }
}
