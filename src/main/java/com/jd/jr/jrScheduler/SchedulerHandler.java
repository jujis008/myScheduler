package com.jd.jr.jrScheduler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.matchers.GroupMatcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 功能描述：
 * <p/>
 * <p/>
 * ----------------------------
 * 姓名：毛祥溢
 * 邮箱：sendmail2mao@gmail.com
 * 网站：www.maoxiangyi.cn
 */
public class SchedulerHandler {

    private static final Log logger = LogFactory.getLog(SchedulerHandler.class);

    private StdScheduler scheduler;

    private List<ScheduleJob> jobList;

    public SchedulerHandler() {
        super();
    }

    public SchedulerHandler(List<ScheduleJob> jobList) {
        this.jobList = jobList;
    }

    public SchedulerHandler(StdScheduler scheduler, List<ScheduleJob> jobList) {
        this.scheduler = scheduler;
        this.jobList = jobList;
    }

    /**
     * 初始化任务
     *
     * @param jobList
     * @throws SchedulerException
     */
    public void initScheduler(Collection<ScheduleJob> jobList) throws SchedulerException {
        if (jobList == null || scheduler == null) {
            logger.error("init error!  jobList is null or scheduler is null.");
            return;
        }
        //初始化任务
        for (ScheduleJob job : jobList) {
            TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
            //获取trigger，即在spring配置文件中定义的 bean id="myTrigger"
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            //不存在，创建一个
            if (null == trigger) {
                JobDetail jobDetail = JobBuilder.newJob(QuartzJobFactory.class).withIdentity(job.getJobName(), job.getJobGroup()).build();
                jobDetail.getJobDataMap().put("scheduleJob", job);
                //表达式调度构建器
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
                //按新的cronExpression表达式构建一个新的trigger
                trigger = TriggerBuilder.newTrigger().withIdentity(job.getJobName(), job.getJobGroup()).withSchedule(scheduleBuilder).build();
                scheduler.scheduleJob(jobDetail, trigger);
            } else {
                // Trigger已存在，那么更新相应的定时设置
                //表达式调度构建器
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
                //按新的cronExpression表达式重新构建trigger
                trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
                //按新的trigger重新设置job执行
                scheduler.rescheduleJob(triggerKey, trigger);
            }
        }
    }

    public Collection<ScheduleJob> prepareTasks() throws Exception {
        if (scheduler == null) {
            logger.error("get prepareTasks error!  jobList is null or scheduler is null.");
            return null;
        }
        GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
        Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
        List<ScheduleJob> jobList = new ArrayList<ScheduleJob>();
        for (JobKey jobKey : jobKeys) {
            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
            for (Trigger trigger : triggers) {
                ScheduleJob job = new ScheduleJob();
                job.setJobName(jobKey.getName());
                job.setJobGroup(jobKey.getGroup());
                job.setDesc("触发器:" + trigger.getKey());
                Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                job.setJobStatus(triggerState.name());
                if (trigger instanceof CronTrigger) {
                    CronTrigger cronTrigger = (CronTrigger) trigger;
                    String cronExpression = cronTrigger.getCronExpression();
                    job.setCronExpression(cronExpression);
                }
                jobList.add(job);
            }
        }
        return jobList;
    }

    public Collection<ScheduleJob> runningTasks() throws Exception {
        if (scheduler == null) {
            logger.error("get runningTasks error!  jobList is null or scheduler is null.");
            return null;
        }
        List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
        List<ScheduleJob> jobList = new ArrayList<ScheduleJob>(executingJobs.size());
        for (JobExecutionContext executingJob : executingJobs) {
            ScheduleJob job = new ScheduleJob();
            JobDetail jobDetail = executingJob.getJobDetail();
            JobKey jobKey = jobDetail.getKey();
            Trigger trigger = executingJob.getTrigger();
            job.setJobName(jobKey.getName());
            job.setJobGroup(jobKey.getGroup());
            job.setDesc("触发器:" + trigger.getKey());
            Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
            job.setJobStatus(triggerState.name());
            if (trigger instanceof CronTrigger) {
                CronTrigger cronTrigger = (CronTrigger) trigger;
                String cronExpression = cronTrigger.getCronExpression();
                job.setCronExpression(cronExpression);
            }
            jobList.add(job);
        }
        return jobList;
    }

    public void pauseJob(ScheduleJob scheduleJob) throws Exception {
        if (scheduler == null || scheduleJob == null) {
            logger.error("pauseJob error!  scheduleJob is null or scheduler is null.");
        }
        JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
        scheduler.pauseJob(jobKey);
    }

    public void resumeJob(ScheduleJob scheduleJob) throws Exception {
        if (scheduler == null || scheduleJob == null) {
            logger.error("resumeJob error!  scheduleJob is null or scheduler is null.");
        }
        JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
        scheduler.resumeJob(jobKey);
    }

    public void deleteJob(ScheduleJob scheduleJob) throws Exception {
        if (scheduler == null || scheduleJob == null) {
            logger.error("deleteJob error!  scheduleJob is null or scheduler is null.");
        }
        JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
        scheduler.deleteJob(jobKey);
    }

    public void triggerJob(ScheduleJob scheduleJob) throws Exception {
        if (scheduler == null || scheduleJob == null) {
            logger.error("triggerJob error!  scheduleJob is null or scheduler is null.");
        }
        JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
        scheduler.triggerJob(jobKey);
    }

    public void updateTrigger(ScheduleJob scheduleJob) throws Exception {
        if (scheduler == null || scheduleJob == null) {
            logger.error("updateTrigger error!  scheduleJob is null or scheduler is null.");
        }
        TriggerKey triggerKey = TriggerKey.triggerKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
        //获取trigger，即在spring配置文件中定义的 bean id="myTrigger"
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        //表达式调度构建器
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression());
        //按新的cronExpression表达式重新构建trigger
        trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
        //按新的trigger重新设置job执行
        scheduler.rescheduleJob(triggerKey, trigger);
    }


    public StdScheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(StdScheduler scheduler) {
        this.scheduler = scheduler;
    }

    public List<ScheduleJob> getJobList() {
        return jobList;
    }

    public void setJobList(List<ScheduleJob> jobList) {
        this.jobList = jobList;
    }
}
