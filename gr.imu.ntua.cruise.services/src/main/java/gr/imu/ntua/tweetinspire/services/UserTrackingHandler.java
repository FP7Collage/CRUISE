package gr.imu.ntua.tweetinspire.services;

import gr.imu.ntua.tweetinspire.services.bean.HeavyUserTracking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 30/04/13
 * Time: 11:11 PM
 */
public class UserTrackingHandler {

    private Logger logger = LoggerFactory.getLogger(UserTrackingHandler.class);
    private ScheduledExecutorService scheduledExecutorService;
    private Map<String,ScheduledFuture<?>> futures;

    private static UserTrackingHandler instance;

    private  UserTrackingHandler(){
        scheduledExecutorService = Executors.newScheduledThreadPool(10);
        futures = new HashMap<String, ScheduledFuture<?>>();
    }

    public void submit(HeavyUserTracking heavyUserTracking) {


        logger.trace("void submit([heavyUserTracking]) {}",this);
        ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(
                heavyUserTracking,
                0, 60, TimeUnit.SECONDS
        );


        futures.put(heavyUserTracking.getId(),scheduledFuture);
        logger.trace("void add([heavyUserTracking]) Adding tracker {} ",heavyUserTracking.getId());
    }

    public void remove(HeavyUserTracking heavyUserTracking){

        logger.trace("void remove([heavyUserTracking]) {}({})" ,heavyUserTracking.getId(),futures.size());

        if(futures.containsKey(heavyUserTracking.getId())){
            futures.get(heavyUserTracking.getId()).cancel(true);
            futures.remove(heavyUserTracking.getId());
        }
        logger.trace("void remove([heavyUserTracking]) Done current size ({})" ,futures.size());
    }



    public void cleanUp() {

        logger.trace("void cleanUp([]) -> {} ",futures.size());
        logger.trace("void sessionDestroyed([httpSessionEvent]) {} -> {}",futures.size());

        //kill all futures
        for (String key : futures.keySet()) {
            logger.trace("void contextDestroyed([servletContextEvent]) Canceling {}", key);
            futures.get(key).cancel(true);
        }

        if(scheduledExecutorService !=null && !scheduledExecutorService.isShutdown()){
            scheduledExecutorService.shutdownNow();
        }


    }

    public static UserTrackingHandler getInstance(){

        if(instance == null){
            instance = new UserTrackingHandler();
        }

        return instance;

    }

}
