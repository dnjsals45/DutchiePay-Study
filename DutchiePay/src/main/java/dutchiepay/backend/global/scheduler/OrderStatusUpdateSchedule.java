package dutchiepay.backend.global.scheduler;

import dutchiepay.backend.domain.commerce.repository.BuyRepository;
import dutchiepay.backend.domain.order.repository.OrderRepository;
import dutchiepay.backend.domain.order.service.OrderService;
import dutchiepay.backend.entity.Buy;
import dutchiepay.backend.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderStatusUpdateSchedule {
    private final OrderService orderService;

    private final OrderRepository orderRepository;

    private final BuyRepository buyRepository;

    private static final String IN_PROGRESS = "공구진행중";
    private static final String PREPARING_SHIPMENT = "배송준비중";
    private static final String SHIPPING = "배송중";
    private static final String COMPLETED = "배송완료";
    private static final String PURCHASE_CONFIRMED = "구매확정";
    private static final String FAILED = "공구실패";
    private static final String EXCHANGE_REQUESTED = "교환요청";

    @Scheduled(cron = "0 5 0 * * ?")
    @Transactional
    public void orderStatusUpdate() {
        log.info("주문 상태 업데이트 스케쥴링 시작");
        List<Buy> buyList = buyRepository.findAll();
        List<Order> changeOrderList = new ArrayList<>();

        for (Buy buy : buyList) {
            Order order = orderRepository.findByBuy(buy);
            if (order == null) {
                continue;
            }
            LocalDate now = LocalDate.now();

            if (buy.getDeadline().isBefore(now) && buy.getNowCount() >= buy.getSkeleton() && IN_PROGRESS.equals(order.getState())) {
                order.changeStatus(PREPARING_SHIPMENT);
            } else if (isEqualOrAfter(now, buy.getDeadline().plusDays(2)) && PREPARING_SHIPMENT.equals(order.getState())) {
                order.changeStatus(SHIPPING);
            } else if ((SHIPPING.equals(order.getState()) || EXCHANGE_REQUESTED.equals(order.getState()))
                    && isEqualOrAfter(now, order.getStatusChangeDate().plusDays(2))) {
                order.changeStatus(COMPLETED);
            } else if (COMPLETED.equals(order.getState()) && isEqualOrAfter(now, order.getStatusChangeDate().plusDays(5))) {
                order.changeStatus(PURCHASE_CONFIRMED);
            } else if (buy.getDeadline().isBefore(now) && buy.getNowCount() < buy.getSkeleton() && IN_PROGRESS.equals(order.getState())) {
                order.changeStatus(FAILED);
                orderService.autoCancelPurchase(order.getOrderId());
            }

            changeOrderList.add(order);
        }

        orderRepository.saveAll(changeOrderList);
        log.info("주문 상태 업데이트 스케쥴링 종료");
    }

    private boolean isEqualOrAfter(LocalDate date1, LocalDate date2) {
        return date1.isEqual(date2) || date1.isAfter(date2);
    }
}
