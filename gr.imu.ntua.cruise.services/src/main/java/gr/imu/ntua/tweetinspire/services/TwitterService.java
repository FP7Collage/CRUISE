package gr.imu.ntua.tweetinspire.services;

import gr.imu.ntua.tweetinspire.services.bean.Tweet;
import twitter4j.TwitterException;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 9/25/12
 * Time: 4:49 PM
 */
public interface TwitterService {

    Map<Long,Tweet> getTweetsFor(boolean join, List<String> from, String ... terms) throws TwitterException;
    Map<Long,Tweet> getTweetsFor(String term, List<String> from, int howMany) throws TwitterException;
}
