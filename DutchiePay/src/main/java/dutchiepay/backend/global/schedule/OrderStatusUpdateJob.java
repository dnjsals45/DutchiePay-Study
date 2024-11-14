package dutchiepay.backend.global.schedule;

import dutchiepay.backend.domain.commerce.repository.BuyRepository;
import dutchiepay.backend.domain.order.repository.OrderRepository;
import dutchiepay.backend.entity.Buy;
import dutchiepay.backend.entity.Order;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderStatusUpdateJob implements Job {

    private final OrderRepository orderRepository;
    private final BuyRepository buyRepository;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        List<Buy> buyList = buyRepository.findAll();

        for (Buy buy : buyList) {
//            Order order = orderRepository.findByBuy(buy);

            if (buy.getDeadline().isBefore(LocalDate.now()) && buy.getNowCount() >= buy.getSkeleton()) {
//                order
            }
        }
    }
}
