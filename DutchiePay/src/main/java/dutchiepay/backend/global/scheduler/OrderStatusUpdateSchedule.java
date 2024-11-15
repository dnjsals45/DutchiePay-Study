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
import java.util.Arrays;
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
        LocalDate now = LocalDate.now();

        updateInProgressOrders(now);
        updatePreparingShipmentOrders(now);
        updateShippingOrders(now);
        updateCompletedOrders(now);

        log.info("주문 상태 업데이트 스케쥴링 종료");
    }

    private void updateInProgressOrders(LocalDate now) {
        List<Order> inProgressOrders = orderRepository.findAllByState(IN_PROGRESS);
        List<Order> updateList = new ArrayList<>();

        for (Order order : inProgressOrders) {
            Buy buy = order.getBuy();
            if (buy.getDeadline().isBefore(now)) {
                if (buy.getNowCount() >= buy.getSkeleton()) {
                    order.changeStatus(PREPARING_SHIPMENT);
                    updateList.add(order);
                } else {
                    order.changeStatus(FAILED);
                    orderService.autoCancelPurchase(order.getOrderId());
                    updateList.add(order);
                }
            }
        }

        if (!updateList.isEmpty()) {
            orderRepository.saveAll(updateList);
        }
    }

    private void updatePreparingShipmentOrders(LocalDate now) {
        List<Order> preparingOrders = orderRepository
                .findPreparingShipmentOrders(PREPARING_SHIPMENT, now.minusDays(2));

        if (!preparingOrders.isEmpty()) {
            preparingOrders.forEach(order -> order.changeStatus(SHIPPING));
            orderRepository.saveAll(preparingOrders);
        }
    }

    private void updateShippingOrders(LocalDate now) {
        List<Order> shippingOrders = orderRepository
                .findShippingOrders(Arrays.asList(SHIPPING, EXCHANGE_REQUESTED),
                        now.minusDays(2));

        if (!shippingOrders.isEmpty()) {
            shippingOrders.forEach(order -> order.changeStatus(COMPLETED));
            orderRepository.saveAll(shippingOrders);
        }
    }

    private void updateCompletedOrders(LocalDate now) {
        List<Order> completedOrders = orderRepository
                .findCompletedOrders(COMPLETED, now.minusDays(5));

        if (!completedOrders.isEmpty()) {
            completedOrders.forEach(order -> order.changeStatus(PURCHASE_CONFIRMED));
            orderRepository.saveAll(completedOrders);
        }
    }
}
