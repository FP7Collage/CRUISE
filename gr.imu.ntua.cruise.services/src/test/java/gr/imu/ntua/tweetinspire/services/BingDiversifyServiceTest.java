package gr.imu.ntua.tweetinspire.services;

import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.env.AbstractEnvironment;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 17/04/13
 * Time: 2:40 PM
 */
public class BingDiversifyServiceTest {

    private BingDiversifyService bingDiversifyService;

    @Before
    public void setup(){

        Properties properties = new Properties();


        properties.put("bing.key","U/7jf84IaAGfeLo/alXGXtSRRI7q1/X9Akt4W2hJFb8=");
        properties.put("bing.id","a48a7ead-cc9c-4c60-94f6-3e4da451dfa8");
        properties.put("bing.uri","https://api.datamarket.azure.com/Bing/Search");
        properties.put("diversify.url","http://147.102.23.40:8089/SerendipitousWebSearch/SearchForDiversity");
        properties.put("flickr.url","http://147.102.23.40:8082/Flickr/FlickrServletDiv");


        bingDiversifyService = new BingDiversifyService();
        bingDiversifyService.setSystemProperties(properties);
//        bingDiversifyService.setEnvironment(
//                new AbstractEnvironment(){
//
//                    Properties properties = new Properties();
//
//
//
//                    @Override
//                    public String getProperty(String key) {
//
//                        //TODO i know this is wrong, but i will look at this later
//
//                        properties.put("bing.key","U/7jf84IaAGfeLo/alXGXtSRRI7q1/X9Akt4W2hJFb8=");
//                        properties.put("bing.id","a48a7ead-cc9c-4c60-94f6-3e4da451dfa8");
//                        properties.put("bing.uri","https://api.datamarket.azure.com/Bing/Search");
//                        properties.put("diversify.url","http://147.102.23.40:8089/SerendipitousWebSearch/SearchForDiversity");
//                        properties.put("flickr.url","http://147.102.23.40:8082/Flickr/FlickrServletDiv");
//
//                        return properties.getProperty(key);
//
//                    }
//
//
//                }
//
//
//        );
    }

    @Test
    public void testJson(){



        try {
            String json = bingDiversifyService.getJson(new String[]{"Collaboration", "Barriers"});
            Assert.assertNotNull(json);
            Assert.assertTrue(json.length() > 0);

          //  System.out.println(json);


        } catch (IOException e) {
            Assert.fail();
        }

    }


    @Test
    public void testParseJson() throws IOException {

        List<BingDiversifyService.Result> results = bingDiversifyService.parseJson(IOUtils.toString(BingDiversifyServiceTest.class.getResource("/bing-results.json")));

        Assert.assertNotNull(results);
        Assert.assertEquals("Not correct size -  ArrayList", results.size() ,50,results.size());

        for(BingDiversifyService.Result r : results){
        //    System.out.println(r);
        }
    }
    @Test
    public void testSendJson() throws IOException {

        List<BingDiversifyService.Result> results = bingDiversifyService.parseJson(IOUtils.toString(BingDiversifyServiceTest.class.getResource("/bing-results.json")));
        List<AbstractDiversifyService.Result> diversify = bingDiversifyService.diversify(results);

        Assert.assertNotNull(results);
        Assert.assertEquals("Not correct size -  ArrayList", results.size(), 50, results.size());

        for(BingDiversifyService.Result r : diversify){
          //  System.out.println(r);
        }
    }

    @Test
    public void testFlickr() throws IOException {

        AbstractDiversifyService.Image[] flickrJson = bingDiversifyService.getFlickrJson(new String[]{"osfp", "basket"});

        Assert.assertNotNull(flickrJson);

        Assert.assertFalse(flickrJson.length <=0 );

    }
}
