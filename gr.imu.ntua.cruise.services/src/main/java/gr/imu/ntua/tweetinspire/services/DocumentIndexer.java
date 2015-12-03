package gr.imu.ntua.tweetinspire.services;

import gr.imu.ntua.tweetinspire.services.bean.SearchTerm;
import gr.imu.ntua.tweetinspire.services.bean.Tweet;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 26/02/13
 * Time: 3:17 PM
 */
public interface DocumentIndexer  {

    void addToIndex(Map<Long, Tweet> tweets);
    List<SearchTerm> search(String terms, List<String> filter, int threshold) throws Exception;

    @PreDestroy
    void cleanUp();
}
