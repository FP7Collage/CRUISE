package gr.imu.ntua.tweetinspire.services;

import com.flickr4java.flickr.FlickrException;
import gr.imu.ntua.tweetinspire.services.bean.SearchTerm;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
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

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * User: fotis
 * Date: 17/04/13
 * Time: 2:17 PM
 */
public class BingDiversifyService extends AbstractDiversifyService {

    private Logger logger = LoggerFactory.getLogger(BingDiversifyService.class);
    private FlickrSimpleAPI flickrSimpleAPI;


    @PostConstruct
    public void init(){

        flickrSimpleAPI = new FlickrSimpleAPI(
                systemProperties.getProperty("flickr.key"),
                systemProperties.getProperty("flickr.secret")
        );

    }
    @Override
    public String getSearchEngineName() {
        return "Bing";
    }

    @Override
    public Map<String, List<Result>> getResultsFor(String[] filter) {

        Map<String,List<Result>> res = new HashMap<String, List<Result>>();
        res.put("original", new ArrayList<Result>());
        res.put("diversified", new ArrayList<Result>());

        int max = Integer.valueOf(systemProperties.getProperty("diversify.maxResults"));

        List<Result> results = new ArrayList<Result>();
        List<Result> diversified = new ArrayList<Result>();

        try {
            results = parseJson(getJson(filter));

            //crop the array
            if(results.size() > max){
                Collections.addAll(res.get("original"),results.subList(0,max).toArray(new Result[max]) );
            }else{
                Collections.addAll(res.get("original"),results.toArray(new Result[results.size()]));
            }

        } catch (IOException e) {
            logger.warn("Couldn't fetch results ",e);

        }



        try {
            diversified = diversify(results);

            if(diversified.size() > max){
                Collections.addAll(res.get("diversified"),diversified.subList(0,max).toArray(new Result[max]) );
            }else{
                Collections.addAll(res.get("diversified"),diversified.toArray(new Result[diversified.size()]) );
            }
        } catch (IOException e) {

            logger.warn("Couldn't diversify results using originals instead");
            Collections.shuffle(results);

            if(results.size() > max){
                Collections.addAll(res.get("diversified"),results.subList(0,max).toArray(new Result[max]));
            }else{
                Collections.addAll(res.get("diversified"),results.toArray(new Result[results.size()]));
            }
        }


        return  res;

    }
    
  @Override
    public List<AbstractDiversifyService.Result> getBingResultsForCloud(String[] filter) {

        Map<String,List<Result>> res = new HashMap<String, List<Result>>();
        res.put("original", new ArrayList<Result>());
        res.put("diversified", new ArrayList<Result>());

        int max = Integer.valueOf(systemProperties.getProperty("diversify.maxResults"));

        List<AbstractDiversifyService.Result> results = new ArrayList<AbstractDiversifyService.Result>();
        List<Result> diversified = new ArrayList<Result>();

        try {
            results = parseJson(getJson(filter));

           } catch (IOException e) {
            logger.warn("Couldn't fetch results ",e);

        }

        return  results;

    }
  
    
     @Override
     public List<SearchTerm> getLinksAsTerms(float maxtermfreq, String terms, int resultsSize) {
         
         List<SearchTerm> searchTerms = new ArrayList<>();
       //  Map<String,List<Result>> bingresults = new HashMap<String, List<Result>>();
        // List<AbstractDiversifyService.Result> bingresults = new ArrayList<AbstractDiversifyService.Result>();
        
         List<AbstractDiversifyService.Result> bingresults = new ArrayList<>(); 
         List<AbstractDiversifyService.Result> results = new ArrayList<>();  
         List<String> queryList = extract(terms);
        final String[] filter = queryList.toArray(new String[queryList.size()]);    
         
         bingresults = getBingResultsForCloud(filter);
      
         for (Integer j = 0 ; j < bingresults.size(); j++){
           // frequencies.put(results.get(j).getId(), (float) ((results.size()-j+1)/results.size())+0.01f );
           
            AbstractDiversifyService.Result i =  bingresults.get(j);
            
            results.add(
                new AbstractDiversifyService.Result(
                        j,
                        i.getLink(),
                        i.getTitle(),
                        i.getSnippet(),                        
                        i.getId().toString()
                )
            );
            j++;
            
        }
         
        try {
            List<AbstractDiversifyService.Result> diversified = diversify(results);
            if(diversified.size() > 20){
                diversified = diversified.subList(0,18);
            }

            List<AbstractDiversifyService.Result> diversifiedLinks = new ArrayList<>();
            for(AbstractDiversifyService.Result r : diversified){

                diversifiedLinks.add(
                        bingresults.get(r.getId())
                );
            }

            bingresults = diversifiedLinks;

        } catch (IOException e) {

            e.printStackTrace();
            logger.warn("Couldn't diversify results so shuffling instead");

            Collections.shuffle(bingresults,new Random(System.nanoTime()));
            //crop the array

        }   

        int iterator=resultsSize;
         
        for (int j = 0; j < bingresults.size(); j++){
            Float  freq;
            freq = (float) getFreq(maxtermfreq, bingresults.size(),j);
            List<String> bingdata = new ArrayList<>();   
            
            bingdata.add( bingresults.get(j).getSnippet());
             searchTerms.add(
                        new SearchTerm(
                               "bing-results",// source,
                                iterator,
                                freq,
                                bingresults.get(j).getTitle(), //here I set the thumbnail url
                                "L"+bingresults.get(j).getLink(), // here I set the image url
                                bingdata ));  
             iterator++;
                
         }
        return searchTerms;
     }
    
    

    @Override
    public List<Image> getImagesFor(String[] filter) {

        int max = 10; //Integer.valueOf(systemProperties.getProperty("diversify.maxResults"));


        List<Image> search = new ArrayList<>();
        try {
            search = flickrSimpleAPI.search(filter);
        } catch (IOException e) {
            logger.warn("Couldn't fetch the exception ",e.getLocalizedMessage());
        } catch (FlickrException e) {
            logger.warn("Couldn't fetch the exception ", e.getErrorMessage());
        }


        List<Result> results = new ArrayList<>();
        for(int id =0; id< search.size(); id++){

            Image i = search.get(id);
            results.add(
                new Result(
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
            List<Result> diversified = diversify(results);
            if(diversified.size() > max){
                diversified = diversified.subList(0,20);
            }

            List<Image> diversifiedImages = new ArrayList<>();
            for(Result r : diversified){

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


    public List<Result> parseJson(String json){

        List<Result> res=new ArrayList<Result>();
        ObjectMapper ob = new ObjectMapper();
        try {
            Map map = ob.readValue(json, Map.class);

            Object results = ((Map)map.get("d")).get("results");
            int i=0;

            if(results instanceof ArrayList){

                for(Object o: (ArrayList)results){

                    Result r = new Result();
                    String title= (String) ((Map) o).get("Title");
                    r.setId(i++);

                    //UID
//                    Map uidMap = (Map) ((ArrayList) o).get(1);
                    r.setUid((String) ((Map) o).get("ID"));

                    //Title
//                    Map titleMap = (Map) ((ArrayList) o).get(2);
                    //r.setTitle(title);
                     if (title.length()>35)
                        r.setTitle((title.replace('"',' ')).substring(0, 35)+"..");
                    else
                        r.setTitle(title.replace('"',' '));

                    //Description
//                    Map descriptionMap = (Map) ((ArrayList) o).get(3);
                    r.setSnippet((String) ((Map) o).get("Description"));

                    //URL
//                    Map urlMap = (Map) ((ArrayList) o).get(5);
                    r.setLink((String) ((Map) o).get("Url"));

                    res.add(r);
                }
            }

        } catch (IOException e) {
            logger.warn("Couldn't parse JSON",e);
        }


        return res;

    }

    public Image[] getFlickrJson(String[] filter) throws IOException {

        HttpPost post = new HttpPost(systemProperties.getProperty("flickr.url"));
        String encode = URLEncoder.encode(StringUtils.join(filter, " "), "UTF-8");
        logger.trace("Image[] getFlickrJson([filter]) Querystr = ({})",encode);

        post.setEntity(
                new StringEntity(
                        "querystr="+encode, ContentType.APPLICATION_FORM_URLENCODED)
        );

        int connectionTimeout =
                Integer.valueOf(systemProperties.getProperty("http.connectionTimeout"));

        HttpParams connectionParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(connectionParameters, connectionTimeout);
        HttpConnectionParams.setSoTimeout(connectionParameters,connectionTimeout);

        DefaultHttpClient dhc = new DefaultHttpClient(connectionParameters);
        HttpResponse execute = dhc.execute(post);
        InputStream content = execute.getEntity().getContent();

        String s = IOUtils.toString(content);
        logger.trace("Image[] getFlickrJson([filter]) {}",s);
        ObjectMapper om = new ObjectMapper();
        Image[] images = om.readValue(s, Image[].class);

         return images;
    }
    
     private Float getFreq(float max, Number size, Number iter){
        
        float s1 = size.floatValue();
        float s2 = iter.floatValue();
        float s3 = Float.MIN_VALUE ;
        float s4 = s1-s2 +1 ;
        float s5=s4/s1;        
        float freq = s5/10;//2*s5*max;//10*s5*max; Na to ftiaksw- exei na kanei me ton arithmo twn apotelesmatwn
      //  System.out.println("Links Frequnecy "+freq);
        
        return freq;
        
        
    }

    public  String getJson(String[] filter) throws IOException {

        String url =systemProperties.getProperty("bing.uri");

        url = url+"/Web?$format=json&Query=";

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
    
    private List<String> extract(Object object) {

        if(object == null || object.toString().trim().length() <= 0){
            return new ArrayList<>();
        }

        String[] split = object.toString().split(",");
        return Arrays.asList(split);

    }

}
