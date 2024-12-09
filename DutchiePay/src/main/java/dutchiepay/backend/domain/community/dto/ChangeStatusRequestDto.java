package dutchiepay.backend.domain.community.dto;

import lombok.Getter;

@Getter
public class ChangeStatusRequestDto {
    private Long postId;
    private String category;
    private String status;
}
