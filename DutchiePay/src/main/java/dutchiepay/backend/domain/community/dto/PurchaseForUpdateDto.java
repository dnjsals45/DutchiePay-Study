package dutchiepay.backend.domain.community.dto;

import com.querydsl.core.Tuple;
import lombok.Builder;
import lombok.Getter;

import static dutchiepay.backend.entity.QPurchase.purchase;

@Getter
@Builder
public class PurchaseForUpdateDto {

    private String title;
    private String category;
    private String content;
    private String goods;
    private Integer price;
    private String meetingPlace;
    private String latitude;
    private String longitude;
    private String thumbnail;
    private String[] images;

    public static PurchaseForUpdateDto toDto(Tuple tuple) {
        String images = tuple.get(purchase.images);
        return PurchaseForUpdateDto.builder()
                .title(tuple.get(purchase.title))
                .category(tuple.get(purchase.category))
                .content(tuple.get(purchase.contents))
                .goods(tuple.get(purchase.goods))
                .price(tuple.get(purchase.price))
                .meetingPlace(tuple.get(purchase.meetingPlace))
                .latitude(tuple.get(purchase.latitude))
                .longitude(tuple.get(purchase.longitude))
                .thumbnail(tuple.get(purchase.thumbnail))
                .images(images != null? images.split(",") : null)
                .build();
    }

}
