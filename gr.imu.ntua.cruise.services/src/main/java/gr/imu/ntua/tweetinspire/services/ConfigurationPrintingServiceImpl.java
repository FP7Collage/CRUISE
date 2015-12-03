package gr.imu.ntua.tweetinspire.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Properties;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 13/09/13
 * Time: 12:56 PM
 */
public class ConfigurationPrintingServiceImpl implements ConfigurationPrintingService {


    private Logger logger = LoggerFactory.getLogger(ConfigurationPrintingServiceImpl.class);

    @Autowired
    Properties systemProperties;


    @PostConstruct
    public void init(){

        Set<Object> objects = systemProperties.keySet();

        logger.info("************************* START System Properties **********************");

        for(Object o: objects){

            String property = systemProperties.getProperty(o.toString());

            logger.info("\t{} = {}",o,property);

        }

        logger.info("*************************  END System Properties **********************\n\n");

    }



}
