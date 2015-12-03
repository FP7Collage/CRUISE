package gr.imu.ntua.tweetinspire.services;

import gr.imu.ntua.tweetinspire.services.bean.UserTracking;
import org.codehaus.jackson.map.ObjectMapper;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 16/05/13
 * Time: 11:22 AM
 */
public class S3SessionPersistService implements SessionPersistService {

    private Logger logger = LoggerFactory.getLogger(S3SessionPersistService.class);

    @Autowired
    Properties systemProperties;
    private S3Service s3Service;
    private S3Bucket cruiseBucket;

    @PostConstruct
    public void init()  {
        try{

            AWSCredentials awsCredentials =
                    new AWSCredentials(systemProperties.getProperty("aws.key"), systemProperties.getProperty("aws.secret"));

                s3Service = new RestS3Service(awsCredentials);
                cruiseBucket = s3Service.getBucket("imu-cruise");
        } catch (S3ServiceException e) {
            logger.warn("S3 Persistance not started session will not be stored");
        }
    }

    @PreDestroy
    public void destroy(){


    }

    @Override
    public void store(UserTracking tracking) throws IOException, S3ServiceException {

        if(s3Service != null){


            S3Object object = new S3Object("session-"+System.currentTimeMillis());


            ByteArrayOutputStream json = new ByteArrayOutputStream();
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(json,tracking);


            byte[] buf = json.toByteArray();
            ByteArrayInputStream jsonin = new ByteArrayInputStream(buf);
            object.setDataInputStream(jsonin);
            object.setContentLength(buf.length);
            object.setContentType("text/plain");
            s3Service.putObject(cruiseBucket,object);

        }else{
            logger.debug("S3 Persistance not started session will not be stored");
        }

    }
}
