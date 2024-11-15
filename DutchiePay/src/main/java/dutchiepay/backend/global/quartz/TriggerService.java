package dutchiepay.backend.global.quartz;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TriggerService {
    @Autowired
    private Scheduler scheduler;

    public void orderStatusUpdateJob() {
        try {
            scheduler.triggerJob(JobKey.jobKey("orderStatusUpdateJob"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
