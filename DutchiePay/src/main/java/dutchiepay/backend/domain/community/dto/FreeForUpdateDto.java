package dutchiepay.backend.domain.community.dto;

import dutchiepay.backend.entity.Free;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FreeForUpdateDto {

    private String title;
    private String content;
    private String category;
    private String thumbnail;
    private String[] images;

    public static FreeForUpdateDto toDto(Free free) {
        return FreeForUpdateDto.builder()
                .title(free.getTitle())
                .content(free.getContents())
                .thumbnail(free.getThumbnail())
                .category(free.getCategory())
                .images(free.getImages() == null ? new String[0] : free.getImages().split(","))
                .build();
    }
}
