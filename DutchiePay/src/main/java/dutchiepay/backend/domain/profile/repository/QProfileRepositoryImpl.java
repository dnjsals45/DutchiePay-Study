package dutchiepay.backend.domain.profile.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
@Slf4j
public class QProfileRepositoryImpl implements QProfileRepository {

    private final JPAQueryFactory jpaQueryFactory;

}
