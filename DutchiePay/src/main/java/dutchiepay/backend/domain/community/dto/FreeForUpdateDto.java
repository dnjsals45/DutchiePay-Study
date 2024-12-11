package dutchiepay.backend.domain.community.dto;

import dutchiepay.backend.entity.ContentImg;
import dutchiepay.backend.entity.Free;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class FreeForUpdateDto {

    private String title;
    private String content;
    private String category;
    private String thumbnail;
    private List<String> images;

    public static FreeForUpdateDto toDto(Free free, List<ContentImg> images) {
        List<String> imges = new ArrayList<>();
        for (ContentImg img : images) {
            imges.add(img.getUrl());
        }
        return FreeForUpdateDto.builder()
                .title(free.getTitle())
                .content(free.getContents())
                .thumbnail(free.getThumbnail())
                .category(free.getCategory())
                .images(imges).build();
    }
}
