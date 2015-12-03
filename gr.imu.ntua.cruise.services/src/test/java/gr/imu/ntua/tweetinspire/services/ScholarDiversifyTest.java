package gr.imu.ntua.tweetinspire.services;

import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 13/09/13
 * Time: 1:54 PM

 */
public class ScholarDiversifyTest {


    @Test
    public void simpleTest() throws IOException {

        Properties p = new Properties();
        p.setProperty("diversify.maxResults","30");
        p.setProperty("http.connectionTimeout","5");
        p.setProperty("diversify.url","http://147.102.23.40:8082/SerendipitousWebSearch/SearchForDiversity");

        ScholarDiversifySerivce service = new ScholarDiversifySerivce();
        service.init();
        service.setSystemProperties(p);
        Map<String,List<AbstractDiversifyService.Result>> resultsFor = service.getResultsFor(new String[]{"expanding cloud", "services"});

        Assert.assertNotNull(resultsFor);
        Assert.assertTrue(resultsFor.containsKey("original"));
        Assert.assertTrue(resultsFor.containsKey("diversified"));

        Assert.assertEquals(resultsFor.get("original").size(),30,0);
        Assert.assertEquals(resultsFor.get("diversified").size(), 30, 0);
    }
}
