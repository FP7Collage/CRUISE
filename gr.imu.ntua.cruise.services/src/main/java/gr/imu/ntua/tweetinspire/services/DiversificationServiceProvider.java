package gr.imu.ntua.tweetinspire.services;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 13/09/13
 * Time: 1:23 PM
  */
public interface DiversificationServiceProvider {
    void addService(String beanName, DiversifyService service);
    DiversifyService getService(String beanName);
}
