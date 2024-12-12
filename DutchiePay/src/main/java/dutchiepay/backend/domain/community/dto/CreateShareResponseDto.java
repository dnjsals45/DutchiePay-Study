package dutchiepay.backend.domain.community.dto;

import dutchiepay.backend.entity.Share;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateShareResponseDto {
    private Long shareId;

    public static CreateShareResponseDto from(Share share) {
        return CreateShareResponseDto.builder()
                .shareId(share.getShareId())
                .build();
    }
}
