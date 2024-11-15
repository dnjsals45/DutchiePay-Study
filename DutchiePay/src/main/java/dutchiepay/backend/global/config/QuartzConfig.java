package dutchiepay.backend.global.config;

import dutchiepay.backend.global.quartz.AutoWiringSpringBeanJobFactory;
import dutchiepay.backend.global.quartz.OrderStatusUpdateJob;
import org.quartz.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail orderStatusUpdateJobDetail() {
        return JobBuilder.newJob(OrderStatusUpdateJob.class)
                .withIdentity("orderStatusUpdateJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger orderStatusUpdateJobTrigger(JobDetail orderStatusUpdateJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(orderStatusUpdateJobDetail)
                .withIdentity("orderStatusUpdateJobTrigger")
                .startNow()
//                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(0, 10))
                .withSchedule(CronScheduleBuilder.cronSchedule("0 * * * * ?"))
                .build();
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(ApplicationContext applicationContext, JobDetail orderStatusUpdateJobDetail, Trigger orderStatusUpdateJobTrigger) {
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        AutoWiringSpringBeanJobFactory autoWiringSpringBeanJobFactory = new AutoWiringSpringBeanJobFactory();

        autoWiringSpringBeanJobFactory.setApplicationContext(applicationContext);
        schedulerFactory.setJobFactory(autoWiringSpringBeanJobFactory);

        schedulerFactory.setApplicationContext(applicationContext);

        schedulerFactory.setJobDetails(orderStatusUpdateJobDetail);
        schedulerFactory.setTriggers(orderStatusUpdateJobTrigger);
        schedulerFactory.start();
        return schedulerFactory;
    }
}