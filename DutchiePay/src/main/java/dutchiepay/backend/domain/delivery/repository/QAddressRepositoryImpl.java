package dutchiepay.backend.domain.delivery.repository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dutchiepay.backend.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class QAddressRepositoryImpl implements QAddressRepository{

    private final JPAQueryFactory jpaQueryFactory;

    QUser qUser = QUser.user;
    QAddress qAddress = QAddress.address;
    QUsersAddress qUsersAddress = QUsersAddress.usersAddress;

    @Override
    public List<Address> findAllByUser(User user) {
        return jpaQueryFactory
                .selectFrom(qAddress)
                .join(qUsersAddress)
                .on(qUsersAddress.address.eq(qAddress))
                .where(qUsersAddress.user.eq(user))
                .fetch();
    }

    @Override
    public void changeOldestAddressToDefault(User user) {
        Address address = jpaQueryFactory
                .selectFrom(qAddress)
                .join(qUsersAddress)
                .on(qUsersAddress.address.eq(qAddress))
                .where(qUsersAddress.user.eq(user))
                .orderBy(qAddress.createdAt.asc())
                .fetchFirst();

        if (address != null) {
            jpaQueryFactory
                    .update(qAddress)
                    .set(qAddress.isDefault, true)
                    .where(qAddress.addressId.eq(address.getAddressId()))
                    .execute();
        }
    }

    @Override
    public void changeIsDefaultTrueToFalse(User user) {
        jpaQueryFactory
                .update(qAddress)
                .set(qAddress.isDefault, false)
                .where(qAddress.isDefault.eq(true)
                        .and(qAddress.addressId.in(
                                JPAExpressions
                                        .select(qUsersAddress.address.addressId)
                                        .from(qUsersAddress)
                                        .where(qUsersAddress.user.eq(user))
                        ))
                )
                .execute();
    }
}
