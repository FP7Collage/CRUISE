package gr.imu.ntua.tweetinspire.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 13/09/13
 * Time: 1:23 PM
 */
public class DiversificationServiceProviderImpl  implements DiversificationServiceProvider{

    private Map<String,DiversifyService> services;


    public DiversificationServiceProviderImpl() {
        this.services= new HashMap<>();
    }

    @Override
    public DiversifyService getService(String beanName){

        if(!this.services.containsKey(beanName)){
            return null;
        }

        return services.get(beanName);

    }

    @Override
    public void addService(String beanName, DiversifyService service){
        this.services.put(beanName,service);
    }

}
