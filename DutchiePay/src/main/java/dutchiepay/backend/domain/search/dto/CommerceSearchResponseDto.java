package dutchiepay.backend.domain.search.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommerceSearchResponseDto {
    String[] tags;
}
