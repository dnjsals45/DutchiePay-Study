package dutchiepay.backend.domain.main.repository;

import com.querydsl.core.Tuple;
import dutchiepay.backend.domain.main.dto.NowHotDto;
import dutchiepay.backend.domain.main.dto.ProductsAndRecommendsDto;

import java.util.List;

public interface QMainRepository {

    List<Tuple> getNewProducts();

    List<Tuple> getRecommends();

    List<Tuple> getNowHot();

}
