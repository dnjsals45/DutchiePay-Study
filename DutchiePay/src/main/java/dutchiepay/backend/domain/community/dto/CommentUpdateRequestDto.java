package dutchiepay.backend.domain.community.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CommentUpdateRequestDto {
    private Long commentId;
    @NotBlank(message = "내용을 입력해주세요.")
    private String content;
}
