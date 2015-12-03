/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.imu.ntua.tweetinspire.services;

import com.flickr4java.flickr.FlickrException;
import gr.imu.ntua.tweetinspire.services.AbstractDiversifyService.Image;
import gr.imu.ntua.tweetinspire.services.bean.SearchTerm;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author imu-user
 */
public class ImagesServiceImpl implements ImagesService {
    
    private Logger logger = LoggerFactory.getLogger(ImagesServiceImpl.class);
    private FlickrSimpleAPI flickrSimpleAPI;
    
    @Autowired
    Properties systemProperties;
   // @Autowired
    //private BingDiversifyService bingDiversify;
    //private FlickrSimpleAPI flickrSimpleAPI;
    @PostConstruct
    
    public void init(){
        //bingDiversify = new BingDiversifyService();
        flickrSimpleAPI = new FlickrSimpleAPI(
                systemProperties.getProperty("flickr.key"),
                systemProperties.getProperty("flickr.secret")
                );
      
    }
    
    
    public List<SearchTerm> getImagesResults(float maxtermfreq, String terms, int resultsSize) {
    
        List<SearchTerm> searchTerms = new ArrayList<>();
        HashMap<Integer, Float> frequencies = new HashMap<Integer, Float>();
        List<String> imagesdata = new ArrayList<>();
        
        
        List<String> queryList = extract(terms);
        final String[] filter = queryList.toArray(new String[queryList.size()]);    
        List<AbstractDiversifyService.Image> search = new ArrayList<AbstractDiversifyService.Image>();
        List<Image> flickrresults = new ArrayList<>();
        
        try {     
            search = parseJson(getJson(filter));
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(ImagesServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        List<AbstractDiversifyService.Result> results = new ArrayList<>();       
        
        imagesdata.add("");       
        
        for (int j = 0; j < search.size(); j++){
           // frequencies.put(results.get(j).getId(), (float) ((results.size()-j+1)/results.size())+0.01f );
           
             AbstractDiversifyService.Image i = search.get(j);
            results.add(
                new AbstractDiversifyService.Result(
                        j,
                        i.getLink(),
                        i.getThumbnail(),
                        i.getTitle(),
                        i.getId()
                )
            );
            j++;
        }
        
         try {
            List<AbstractDiversifyService.Result> diversified = diversify(results);
            if(diversified.size() > 20){
                diversified = diversified.subList(0,16);
            }

            List<AbstractDiversifyService.Image> diversifiedImages = new ArrayList<>();
            for(AbstractDiversifyService.Result r : diversified){

                diversifiedImages.add(
                        search.get(r.getId())
                );
            }

            search = diversifiedImages;

        } catch (IOException e) {

            e.printStackTrace();
            logger.warn("Couldn't diversify results so shuffling instead");

            Collections.shuffle(search,new Random(System.nanoTime()));
            //crop the array

        }   

        int iterator=resultsSize;
        for (int j = 0; j < search.size(); j++){
            Float  freq;
            freq = (float) getFreq(maxtermfreq, search.size(),j);

         
             searchTerms.add(
                        new SearchTerm(
                               "image-results",// source,
                                iterator,
                                freq,
                                search.get(j).getThumbnail(), //here I set the thumbnail url                                
                                "I"+search.get(j).getLink(), // here I set the image url
                                imagesdata )); 
             iterator++;
                
         }
        
        
        flickrresults = getImagesFor(filter);
        //System.out.println("What is going on here?");
        int iterator2=resultsSize+search.size();
        for (int id =0; id < flickrresults.size(); id++){
            Float  freq;
            freq = (float) getFreq(maxtermfreq, search.size(),id);

         
             searchTerms.add(
                        new SearchTerm(
                               "image-results",// source,
                                iterator2,
                                freq,
                                flickrresults.get(id).getThumbnail(), //here I set the thumbnail url                               
                                "I"+flickrresults.get(id).getLink(),                                
                                imagesdata ));         
                
         }
        iterator2++;
        
        
         return searchTerms;
    
    }
    
    private Float getFreq(float max, Number size, Number iter){
        
        float s1 = size.floatValue();
        float s2 = iter.floatValue();
        float s3 = Float.MIN_VALUE ;
        float s4 = s1-s2 +1 ;
        float s5=s4/s1;        
        float freq = s5/5;//s5*2*max;//10*s5*max;
       // System.out.println("Image freq " + freq);
        
        return freq;
        
        
    }
    
    private  List<Image> getImagesFor(String[] filter) {

        int max = 6; //Integer.valueOf(systemProperties.getProperty("diversify.maxResults"));


        List<Image> search = new ArrayList<>();
        try {
            search = flickrSimpleAPI.search(filter);
        } catch (IOException e) {
            logger.warn("Couldn't fetch the exception ",e.getLocalizedMessage());
        } catch (FlickrException e) {
            logger.warn("Couldn't fetch the exception ", e.getErrorMessage());
        }


        List<AbstractDiversifyService.Result> results = new ArrayList<>();
        for(int id =0; id< search.size(); id++){

            Image i = search.get(id);
            results.add(
                new AbstractDiversifyService.Result(
                        id,
                        i.getLink(),
                        i.getThumbnail(),
                        i.getTitle(),
                        i.getId()
                )
            );
            id++;
        }




        try {
            List<AbstractDiversifyService.Result> diversified = diversify(results);
            if(diversified.size() > max){
                diversified = diversified.subList(0,max);
            }

            List<Image> diversifiedImages = new ArrayList<>();
            for(AbstractDiversifyService.Result r : diversified){

                diversifiedImages.add(
                        search.get(r.getId())
                );

            }

            search = diversifiedImages;

        } catch (IOException e) {

            e.printStackTrace();
            logger.warn("Couldn't diversify results using originals instead");

            Collections.shuffle(search,new Random(System.nanoTime()));
            //crop the array
            if(search.size() > max){
                search  = search.subList(0,max);
            }
        }


        return search;
    }

                       
    private  List<AbstractDiversifyService.Result> diversify(List<AbstractDiversifyService.Result> resultList) throws IOException {

        OutputStream o = new ByteArrayOutputStream();


        Map<String,List<AbstractDiversifyService.Result>> results = new HashMap<String, List<AbstractDiversifyService.Result>>();
        results.put("items",resultList);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(o,results);

        String json = o.toString();
        logger.trace("List<Result> diversify([resultList]) {} ",json);



        HttpPost post = new HttpPost(systemProperties.getProperty("diversify.url"));
        post.setEntity(
                new StringEntity(json, ContentType.APPLICATION_JSON)
        );

        int connectionTimeout =
                Integer.valueOf(systemProperties.getProperty("http.connectionTimeout"));

        HttpParams connectionParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(connectionParameters,connectionTimeout);
        HttpConnectionParams.setSoTimeout(connectionParameters,connectionTimeout);

        HttpClient c = new DefaultHttpClient(connectionParameters);

        long start = System.currentTimeMillis();
        HttpResponse execute = c.execute(post);
        String resultIds = IOUtils.toString(execute.getEntity().getContent());

        long time = System.currentTimeMillis() - start;
        logger.debug("List<Result> diversify([resultList]) Diversification took {} ms \n\t{}",time,resultIds);
        List idsList = objectMapper.readValue(resultIds, List.class);
        List<AbstractDiversifyService.Result> diversified = new ArrayList<AbstractDiversifyService.Result>();

        for(Object object: idsList){

            Integer id1 = (Integer) ((Map) object).get("id");
            AbstractDiversifyService.Result id = resultList.get(id1);
            diversified.add(id);
        }

        return diversified;

    }    
    
     public  String getJson(String[] filter) throws IOException {

        String url =systemProperties.getProperty("bing.uri");

        url = url+"/Image?$format=json&Query=";

        String query = "'"+StringUtils.join(filter," ")+"'"; // By Microsofts Standard Query needs to be a string
                                                             // inside single quotes

        try {
            query = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {}


        url = url+query;


        DefaultHttpClient httpclient = new DefaultHttpClient();
        httpclient.getCredentialsProvider().setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials(systemProperties.getProperty("bing.key"), systemProperties.getProperty("bing.key"))
        );
        //"https://api.datamarket.azure.com/Bing/Search/Web?$format=json&Query=%27Xbox%27"

        logger.trace("String getJson([filter]) {} ",url);

        HttpPost post = new HttpPost(url);

        HttpResponse response = httpclient.execute(post);
        
       

        return IOUtils.toString(response.getEntity().getContent());
    }

    public List<AbstractDiversifyService.Image> parseJson(String json){

        List<AbstractDiversifyService.Image> res=new ArrayList<AbstractDiversifyService.Image>();
        ObjectMapper ob = new ObjectMapper();
        try {
            Map map = ob.readValue(json, Map.class);

            Object results = ((Map)map.get("d")).get("results");
            int i=0;

            if(results instanceof ArrayList){

                for(Object o: (ArrayList)results){

                    AbstractDiversifyService.Image r = new AbstractDiversifyService.Image();
                 //   r.setId(i++);

//                    Map uidMap = (Map) ((ArrayList) o).get(1);
                    r.setId((String) ((Map) o).get("ID"));

                    //Title
//                    Map titleMap = (Map) ((ArrayList) o).get(2);
                    r.setTitle((String) ((Map) o).get("Title"));

                    //Description
//                    Map descriptionMap = (Map) ((ArrayList) o).get(3);
                    r.setLink((String) ((Map) o).get("MediaUrl")); //I am giving the actual url of the image

                    //URL
                   
                  //  Object image = ((Map) o.get("Thumbnail"));
                    r.setThumbnail(  (String) ( ((Map) ((Map) o).get("Thumbnail") ).get("MediaUrl") ));
                                          
                                    

                    res.add(r);
                }
            }

        } catch (IOException e) {
            logger.warn("Couldn't parse JSON",e);
        }

        return res;

    }
    
    private List<String> extract(Object object) {

        if(object == null || object.toString().trim().length() <= 0){
            return new ArrayList<>();
        }

        String[] split = object.toString().split(",");
        return Arrays.asList(split);

    }

    
}
