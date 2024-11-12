package dutchiepay.backend.domain.main.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MainResponseDto {

    private List<ProductsAndRecommendsDto> newProducts;
    private List<ProductsAndRecommendsDto> recommends;
    private List<NowHotDto> nowHot;
}
