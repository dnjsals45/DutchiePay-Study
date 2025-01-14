package dutchiepay.backend.domain.chat.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CursorResponse {
    private String type;
    private Long cursor;

    public static CursorResponse of(Long cursor) {
        return CursorResponse.builder()
                .type("cursor")
                .cursor(cursor)
                .build();
    }
}
