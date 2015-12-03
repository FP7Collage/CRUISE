/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.imu.ntua.tweetinspire.services;

import gr.imu.ntua.tweetinspire.services.bean.UserTracking;
import java.io.IOException;
import org.jets3t.service.S3ServiceException;

/**
 *
 * @author imu-user
 */
public class FakePersistService implements SessionPersistService {
    
    public void store(UserTracking tracking) throws IOException, S3ServiceException 
    {
    
    }
    
    
    
    
}
