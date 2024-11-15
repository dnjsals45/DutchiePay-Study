package dutchiepay.backend.global.quartz;

import dutchiepay.backend.domain.commerce.repository.BuyRepository;
import dutchiepay.backend.domain.order.repository.OrderRepository;
import dutchiepay.backend.domain.order.service.OrderService;
import dutchiepay.backend.entity.Buy;
import dutchiepay.backend.entity.Order;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class OrderStatusUpdateJob implements Job {

    @Autowired
    private final OrderService orderService;
    @Autowired
    private final OrderRepository orderRepository;
    @Autowired
    private final BuyRepository buyRepository;

    public OrderStatusUpdateJob() {
        this.orderService = null;
        this.orderRepository = null;
        this.buyRepository = null;
    }

    private static final String IN_PROGRESS = "공구진행중";
    private static final String PREPARING_SHIPMENT = "배송준비중";
    private static final String SHIPPING = "배송중";
    private static final String COMPLETED = "배송완료";
    private static final String PURCHASE_CONFIRMED = "구매확정";
    private static final String FAILED = "공구실패";
    private static final String EXCHANGE_REQUESTED = "교환요청";

    @Override
    @Transactional
    public void execute(JobExecutionContext jobExecutionContext) {
        assert buyRepository != null;
        List<Buy> buyList = buyRepository.findAll();
        List<Order> changeOrderList = new ArrayList<>();

        for (Buy buy : buyList) {
            assert orderRepository != null;
            Order order = orderRepository.findByBuy(buy);
            if (order == null) {
                continue;
            }
            LocalDate now = LocalDate.now();

            if (buy.getDeadline().isBefore(now) && buy.getNowCount() >= buy.getSkeleton() && IN_PROGRESS.equals(order.getState())) {
                order.changeStatus(PREPARING_SHIPMENT);
            } else if (buy.getDeadline().plusDays(2).isBefore(now) && PREPARING_SHIPMENT.equals(order.getState())) {
                order.changeStatus(SHIPPING);
            } else if ((SHIPPING.equals(order.getState()) || EXCHANGE_REQUESTED.equals(order.getState()))
                    && isEqualOrAfter(order.getStatusChangeDate().plusDays(2), now)) {
                order.changeStatus(COMPLETED);
            } else if (SHIPPING.equals(order.getState()) && isEqualOrAfter(order.getStatusChangeDate().plusDays(7) ,now)) {
                order.changeStatus(PURCHASE_CONFIRMED);
            } else if (buy.getDeadline().isBefore(now) && buy.getNowCount() < buy.getSkeleton() && IN_PROGRESS.equals(order.getState())) {
                order.changeStatus(FAILED);
                assert orderService != null;
                orderService.autoCancelPurchase(order.getOrderId());
            }

            changeOrderList.add(order);
        }

        assert orderRepository != null;
        orderRepository.saveAll(changeOrderList);
    }

    private boolean isEqualOrAfter(LocalDate date1, LocalDate date2) {
        return date1.isEqual(date2) || date1.isAfter(date2);
    }
}
