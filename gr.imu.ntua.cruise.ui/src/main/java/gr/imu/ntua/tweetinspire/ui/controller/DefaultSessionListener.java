package gr.imu.ntua.tweetinspire.ui.controller;

import gr.imu.ntua.tweetinspire.services.UserTrackingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 30/04/13
 * Time: 4:47 PM
 */
public class DefaultSessionListener implements ServletContextListener {

    private Logger logger = LoggerFactory.getLogger(DefaultSessionListener.class);


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        // grab the application context
        UserTrackingHandler.getInstance().cleanUp();

    }
}
