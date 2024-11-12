package dutchiepay.backend.domain.main.repository;

import com.querydsl.core.Tuple;

import java.util.List;

public interface QMainRepository {

    List<Tuple> getNewProducts();

    List<Tuple> getRecommends();

    List<Tuple> getNowHot();

}
