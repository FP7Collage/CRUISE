package gr.imu.ntua.tweetinspire.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 13/09/13
 * Time: 1:20 PM
 */
public class DiversificationServicesPostProcessor implements BeanPostProcessor {

    private Logger logger = LoggerFactory.getLogger(DiversificationServicesPostProcessor.class);

    @Autowired
    private DiversificationServiceProvider diversificationServiceProvider;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        if(bean instanceof DiversifyService){
            logger.debug("Adding diversification service {}",bean);
            diversificationServiceProvider.addService(beanName.replace("DiversificationService",""), (DiversifyService) bean);
        }

        return bean;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
