package gr.imu.ntua.tweetinspire.services;

import gr.imu.ntua.tweetinspire.services.bean.Tweet;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import twitter4j.TwitterException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 9/25/12
 * Time: 5:53 PM
 */
public class TwitterServiceTest {


    TwitterServiceImpl twitterService;

    @Before
    public void before() throws IOException {
        Properties props = new Properties();
        props.load(TwitterServiceTest.class.getResourceAsStream("/system.properties"));
        twitterService = new TwitterServiceImpl();
        twitterService.setSystemProperties(props);
        twitterService.init();

    }


    @Test
    public void testSimpleAccess() throws TwitterException {

        Map<Long,Tweet> nike = twitterService.getTweetsFor("nike",null,100);
        Assert.assertNotNull(nike);
        Assert.assertTrue(nike.size() > 0);
    }

    @Test
    public void testComplexQuery() throws TwitterException {

        List<String> from= new ArrayList<>();

        from.add("@thishappenednl");
        from.add("@thishappenedbrs");

        Map<Long,Tweet> nike = twitterService.getTweetsFor("design",from,100);
        Assert.assertNotNull(nike);

        Assert.assertTrue(nike.size() > 0);


    }


}

