package com.secqme.util;

import com.secqme.util.spring.DefaultSpringUtil;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

/**
 * User: James Khoo
 * Date: 10/1/14
 * Time: 11:47 AM
 */
public class CoreContextListener implements ServletContextListener {

    private static Logger myLog = Logger.getLogger(CoreContextListener.class);

    public void contextInitialized(ServletContextEvent servletCtxEvent) {
        try {
            WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletCtxEvent.getServletContext());
            DefaultSpringUtil.getInstance().setApplicationContext(ctx);
            myLog.debug("Initializing all require resources..");
            myLog.debug("Initializing JPA Entity Factory");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void contextDestroyed(ServletContextEvent arg0) {
        myLog.debug("Cleaning up all resources..");
        myLog.debug("Stopping all schedule job");
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                myLog.debug(String.format("deregistering jdbc driver: %s", driver));
            } catch (SQLException e) {
                myLog.error(String.format("Error deregistering driver %s", driver), e);
            }
        }
//        myLog.debug("Closing JPA Entity Factory..");
//        EntityManagerFactory emf = (EntityManagerFactory) DefaultSpringUtil.getInstance().getBean(BeanType.entityManagerFactory);
//        emf.close();


    }
}
