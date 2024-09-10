package dutchiepay.backend.domain.commerce;

import dutchiepay.backend.domain.profile.exception.ProfileErrorCode;
import dutchiepay.backend.domain.profile.exception.ProfileErrorException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum BuyCategory {
    잡화("잡화", "0"),
    가구("인테리어/가구", "1"),
    보안("보안용품", "2"),
    가전("디지털/가전", "3"),
    미용("화장품/미용", "4"),
    패브릭("패브릭", "5"),
    냉동("냉동", "6"),
    신선("신선/가공", "7"),
    주방("주방/청소용품", "8"),
    생활("생활용품", "9");

    private final String category;
    private final String code;

    public static BuyCategory ofCategory(String dbData) {
        return Arrays.stream(BuyCategory.values())
                .filter(v -> v.getCode().equals(dbData))
                .findAny()
                .orElseThrow(() -> new ProfileErrorException(ProfileErrorCode.INVALID_CATEGORY));
    }

    public static BuyCategory fromCategoryName(String categoryName) {
        return Arrays.stream(BuyCategory.values())
                .filter(v -> v.getCategory().equals(categoryName))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("일치하는 카테고리명이 없습니다 %s", categoryName)));
    }
}
