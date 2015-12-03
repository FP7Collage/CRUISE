package gr.imu.ntua.tweetinspire.services;

import com.flickr4java.flickr.FlickrException;
import org.junit.Test;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 04/09/13
 * Time: 11:04 AM
 */
public class FlickrSimpleAPITest {


    @Test
    public void simple() throws IOException, FlickrException {
        FlickrSimpleAPI api = new FlickrSimpleAPI(
                "0974852136e7b18b0dadd82bd93e9379",
                "dd5250f276317368"
        );


        api.search("hello world");
    }
}
