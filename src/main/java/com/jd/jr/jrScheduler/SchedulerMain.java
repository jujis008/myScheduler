package com.jd.jr.jrScheduler;

import org.quartz.impl.StdScheduler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Collection;
import java.util.List;

/**
 * 项目描述：
 * <p/>
 * Spring 3和Quartz 2组合已经能够满足很多场景下的应用。
 * 但大多数时候，我们期望能够动态的添加或修改任务，然而spring提供的定时任务组件一般都是通过xml配置文件设定的，需要通过修改xml中trigger的配置才能控制定时任务的时间以及任务的启用或停止。
 * 这在带给我们方便的同时也失去了动态配置任务的灵活性。
 * 这个项目的目标是基于Spring 3和Quartz 2实现任务的动态操作。
 * <p/>
 * <p/>
 * <p/>
 * 功能描述：
 * <p/>
 * ----------------------------
 * 姓名：毛祥溢
 * 邮箱：sendmail2mao@gmail.com
 * 网站：www.maoxiangyi.cn
 */
public class SchedulerMain {
    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        SchedulerHandler schedulerHandler = (SchedulerHandler) context.getBean("schedulerHandler");
        schedulerHandler.initScheduler(DataWorkContext.getAllJob());
        Collection<ScheduleJob> jobList = schedulerHandler.prepareTasks();
        for (ScheduleJob scheduleJob : jobList) {
            System.out.println(scheduleJob);
        }
        System.out.println();
        System.out.println();
        jobList = schedulerHandler.runningTasks();
        for (ScheduleJob scheduleJob : jobList) {
            System.out.println(scheduleJob);
        }

        ScheduleJob job = new ScheduleJob();
        job.setJobId("100011");
        job.setJobName("data_import1");
        job.setJobGroup("dataWork");
        job.setJobStatus("1");
        job.setCronExpression("0/5 * * * * ?");
        job.setDesc("数据导入任务");

        schedulerHandler.pauseJob(job);

        ScheduleJob job1 = new ScheduleJob();
        job1.setJobId("100012");
        job1.setJobName("data_import2");
        job1.setJobGroup("dataWork");
        job1.setJobStatus("1");
        job1.setCronExpression("0/5 * * * * ?");
        job1.setDesc("数据导入任务");

        schedulerHandler.deleteJob(job1);

    }


}
