package dutchiepay.backend.domain.commerce;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum BuyPostCategory {
    잡화("잡화"),
    인테리어("인테리어/가구"),
    보안용품("보안용품"),
    디지털("디지털/가전"),
    화장품("화장품/미용"),
    패브릭("패브릭"),
    냉동("냉동"),
    신선("신선/가공"),
    주방("주방/청소용품"),
    생활용품("생활용품");

    private final String category;
}
