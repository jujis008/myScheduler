package com.jd.jr.jrScheduler;

import org.quartz.Scheduler;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by maoxiangyi on 14-7-22.
 */
public class DataWorkContext {
    /** 计划任务map */
    private static Map<String, ScheduleJob> jobMap = new HashMap<String, ScheduleJob>();

    static {
        for (int i = 0; i < 5; i++) {
            ScheduleJob job = new ScheduleJob();
            job.setJobId("10001" + i);
            job.setJobName("data_import" + i);
            job.setJobGroup("dataWork");
            job.setJobStatus("1");
            job.setCronExpression("0/5 * * * * ?");
            job.setDesc("数据导入任务");
            addJob(job);
        }
    }

    /**
     * 添加任务
     * @param scheduleJob
     */
    public static void addJob(ScheduleJob scheduleJob) {
        jobMap.put(scheduleJob.getJobGroup() + "_" + scheduleJob.getJobName(), scheduleJob);
    }

    public static Collection getAllJob(){
        return jobMap.values();
    }
}
