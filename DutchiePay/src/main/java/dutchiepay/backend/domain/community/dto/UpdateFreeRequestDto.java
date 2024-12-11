package dutchiepay.backend.domain.community.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class UpdateFreeRequestDto {

    @NotNull(message = "게시글을 식별할 수 없습니다.")
    private Long freeId;
    @NotBlank(message = "제목이 입력되지 않았습니다.")
    private String title;
    private String content;
    @NotBlank(message = "카테고리가 지정되지 않았습니다.")
    private String category;
    private String thumbnail;
    private List<String> images;
}
