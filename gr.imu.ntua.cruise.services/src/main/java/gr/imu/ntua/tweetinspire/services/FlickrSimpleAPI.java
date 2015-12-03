package gr.imu.ntua.tweetinspire.services;

import com.flickr4java.flickr.*;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.SearchParameters;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import static com.flickr4java.flickr.Transport.*;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 04/09/13
 * Time: 10:53 AM
 */
public class FlickrSimpleAPI {

    private Logger logger = LoggerFactory.getLogger(FlickrSimpleAPI.class);

    private final String API_ENDPOINT="https://secure.flickr.com/services";
    private String key;
    private String secret;
    private final Flickr flickr;

    public FlickrSimpleAPI(String key, String secret) {
        this.key = key;
        this.secret = secret;

        flickr = new Flickr(
                key,
                secret,
                new com.flickr4java.flickr.REST());
    }


    public List<AbstractDiversifyService.Image> search(String ...search) throws IOException, FlickrException {

        if(search == null || search.length <=0){
            return new ArrayList<>();
        }

        String query = "";
        if(search.length==1){
            query = search[0].replaceAll(" ",",");
        }else{

            query = StringUtils.join(search,",");
            query += query.replaceAll(" ",",");
        }




        SearchParameters searchParameters = new SearchParameters();
        Set<String> extras  =new HashSet<>();
        extras.add("url_s");
        extras.add("url_t");

        searchParameters.setExtras(extras);
        searchParameters.setTags(query.split(","));
        searchParameters.setMedia("photos");

        PhotoList<Photo> search1 = flickr.getPhotosInterface().search(searchParameters,100,1);

        List<AbstractDiversifyService.Image> ret = new ArrayList<>();

        for (Photo p : search1) {

            ret.add(new AbstractDiversifyService.Image(
                    p.getId(),
                    p.getTitle(),
                    p.getThumbnailUrl(),
                    p.getMedium800Url()
            ));
        }

        return ret;

    }
}
