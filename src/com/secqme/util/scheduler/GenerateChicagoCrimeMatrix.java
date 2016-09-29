package com.secqme.util.scheduler;

import com.secqme.crimedata.manager.CrimeManager;
import com.secqme.crimedata.manager.DataManager;
import com.secqme.util.spring.BeanType;
import com.secqme.util.spring.DefaultSpringUtil;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Created by Edmund on 10/8/15.
 */
public class GenerateChicagoCrimeMatrix implements Job {
    private static CrimeManager crimeManager = (CrimeManager) DefaultSpringUtil.getInstance().getBean(BeanType.CRIME_MANAGER);
    private static DataManager dataManager = (DataManager) DefaultSpringUtil.getInstance().getBean(BeanType.DATA_MANAGER);
    private static final String CHICAGO = "chicago";
    private Logger myLog = Logger.getLogger(GenerateChicagoCrimeMatrix.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        crimeManager.generateCityCrimeMatrix(CHICAGO,false);
    }
}
