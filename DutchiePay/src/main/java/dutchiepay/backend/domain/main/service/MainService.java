package dutchiepay.backend.domain.main.service;

import dutchiepay.backend.domain.main.dto.MainResponseDto;
import dutchiepay.backend.domain.main.dto.NowHotDto;
import dutchiepay.backend.domain.main.dto.ProductsAndRecommendsDto;
import dutchiepay.backend.domain.main.repository.QMainRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static dutchiepay.backend.entity.QBuy.buy;
import static dutchiepay.backend.entity.QProduct.product;

@Service
@RequiredArgsConstructor
public class MainService {

    private final QMainRepositoryImpl qMainRepository;
    public MainResponseDto getMain() {

        List<ProductsAndRecommendsDto> newProducts = qMainRepository.getNewProducts().stream()
                .map(tuple -> ProductsAndRecommendsDto.builder()
                        .buyId(tuple.get(buy.buyId))
                        .productName(tuple.get(product.productName))
                        .productImg(tuple.get(product.productImg))
                        .productPrice(tuple.get(product.originalPrice))
                        .discountPrice(tuple.get(product.salePrice))
                        .discountPercent(tuple.get(product.discountPercent))
                        .expireDate(tuple.get(buy.deadline))
                        .build())
                .toList();

        List<ProductsAndRecommendsDto> recommends = qMainRepository.getRecommends().stream()
                .map(tuple -> ProductsAndRecommendsDto.builder()
                        .buyId(tuple.get(buy.buyId))
                        .productName(tuple.get(product.productName))
                        .productImg(tuple.get(product.productImg))
                        .productPrice(tuple.get(product.originalPrice))
                        .discountPrice(tuple.get(product.salePrice))
                        .discountPercent(tuple.get(product.discountPercent))
                        .expireDate(tuple.get(buy.deadline))
                        .build())
                .toList();

        List<NowHotDto> nowHot = qMainRepository.getNowHot().stream()
                .map(tuple -> NowHotDto.builder()
                        .buyId(tuple.get(buy.buyId))
                        .productName(tuple.get(product.productName))
                        .productImg(tuple.get(product.productImg))
                        .productPrice(tuple.get(product.originalPrice))
                        .discountPrice(tuple.get(product.salePrice))
                        .discountPercent(tuple.get(product.discountPercent))
                        .skeleton(tuple.get(buy.skeleton))
                        .nowCount(tuple.get((buy.nowCount)))
                        .expireDate(tuple.get(buy.deadline))
                        .build())
                .toList();

        return MainResponseDto.builder().newProducts(newProducts).recommends(recommends).nowHot(nowHot).build();
    }
}
