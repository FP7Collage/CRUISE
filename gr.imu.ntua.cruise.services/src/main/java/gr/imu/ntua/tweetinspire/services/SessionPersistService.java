package gr.imu.ntua.tweetinspire.services;

import gr.imu.ntua.tweetinspire.services.bean.UserTracking;
import org.jets3t.service.S3ServiceException;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 16/05/13
 * Time: 11:21 AM
 */
public interface SessionPersistService {

    void store(UserTracking tracking) throws IOException, S3ServiceException;
}
