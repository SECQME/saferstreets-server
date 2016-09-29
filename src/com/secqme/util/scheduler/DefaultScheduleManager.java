package com.secqme.util.scheduler;

import org.apache.log4j.Logger;
import org.quartz.*;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.CronScheduleBuilder.cronSchedule;

/**
 * Created by Edmund on 10/7/15.
 */
public class DefaultScheduleManager implements ScheduleManager {
    private static final Logger myLog = Logger.getLogger(DefaultScheduleManager.class);
    private static final String CHICAGO_CRIME_MATRIX_KEY = "chicago_crime_matrix_key";
    private static final String CHICAGO_CRIME_MATRIX_QUEUE_GROUP = "chicago_crime_matrix_queue_group";
    private static final String CHICAGO_CRIME_MATRIX_TRIGGER_KEY = "chicago_crime_matrix_trigger_key";

    public String chicagoCrimeMatrixCronSchedule;

    private static Scheduler scheduler;

    @Override
    public void initScheduleManager() {
//        try {
//            myLog.debug("Preparing to start crime matrix scheduler");
//            scheduler = StdSchedulerFactory.getDefaultScheduler();
//            scheduler.start();
//            myLog.debug(scheduler.getSchedulerName()+ " scheduler started");
//        }catch(SchedulerException ex){
//            myLog.debug("Error on starting the Quartz Scheduler : " + ex);
//        }

    }

    @Override
    public void shutdown() {
        try {
            myLog.debug("Preparing to shutdown the  Quartz Scheduler");
            scheduler.shutdown(true);
            myLog.debug(scheduler.getSchedulerName() + " is shut down");
        } catch (SchedulerException ex) {
            myLog.error("Error on shutting down the Quartz Scheduler..", ex);
        }
    }

    private void generateCrimeMatrixChicago(){
        myLog.debug("Generating Crime Matrix for Chicago");
        try{
            JobKey jobKey = new JobKey(CHICAGO_CRIME_MATRIX_KEY, CHICAGO_CRIME_MATRIX_QUEUE_GROUP);
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
            }
            JobDetail myJob = newJob(GenerateChicagoCrimeMatrix.class)
                    .withIdentity(CHICAGO_CRIME_MATRIX_KEY, CHICAGO_CRIME_MATRIX_QUEUE_GROUP)
                    .build();

            Trigger trigger = newTrigger()
                    .withIdentity(CHICAGO_CRIME_MATRIX_TRIGGER_KEY, CHICAGO_CRIME_MATRIX_QUEUE_GROUP)
                    .withSchedule(cronSchedule(chicagoCrimeMatrixCronSchedule))
                    .forJob(myJob)
                    .build();
            scheduler.scheduleJob(myJob, trigger);
        }catch(SchedulerException se){
            myLog.debug("Chicago Crime Matrix Scheduler ain't runnning : " + se);
        }
    }

}
