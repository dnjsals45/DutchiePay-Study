package dutchiepay.backend.domain.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum NicknamePrefix {
    멋진("멋진"),
    신기한("신기한"),
    대단한("대단한"),
    놀라운("놀라운"),
    기쁜("기쁜"),
    행복한("행복한"),
    슬픈("슬픈"),
    화난("화난"),;

    private final String prefix;

    public static NicknamePrefix of(Long userId) {
        int index = (int) (userId % values().length);
        return values()[index];
    }
}
