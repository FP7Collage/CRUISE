/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.imu.ntua.tweetinspire.services;


import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import gr.imu.ntua.tweetinspire.services.bean.SearchTerm;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import javax.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 *
 * @author imu-user
 */


/*
 * Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */


/**
 * Print a list of videos matching a search term.
 *
 * @author Jeremy Walker
 */
public class YouTubeServiceImpl implements YouTubeService {

    /**
     * Define a global variable that identifies the name of a file that
     * contains the developer's API key.
     */
    private Logger logger = LoggerFactory.getLogger(ImagesServiceImpl.class);
    @Autowired
    Properties systemProperties;
    public void init(){
        
    }
 
    public List<SearchTerm> getVideosFor(float maxtermfreq,String terms, int resultsSize){
    
    List<SearchTerm> searchTerms = new ArrayList<>();
    HashMap<Integer, Float> frequencies = new HashMap<Integer, Float>();
    List<String> videodata = new ArrayList<>();
        
        
    List<String> queryList = extract(terms);
    final String[] filter = queryList.toArray(new String[queryList.size()]);    
    List<AbstractDiversifyService.Video> search = new ArrayList<AbstractDiversifyService.Video>();
    
    
    try {     
            search = parseJson(getJson(filter));
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(ImagesServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            java.util.logging.Logger.getLogger(YouTubeServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }   
         
         int iterator=resultsSize;
         
         for (Integer id = 0 ; id < search.size(); id++){
            Float  freq;
            freq = (float) getFreq(maxtermfreq, search.size(),id);
            List<String> youtubedata = new ArrayList<>();   
            
            youtubedata.add( search.get(id).getSnippet());
         
             searchTerms.add(
                     new SearchTerm(
                               "image-results",// source,
                                iterator,
                                freq,
                                search.get(id).getThumbnail(),//"YouTube: "+search.get(id).getTitle(), //here I set the thumbnail url
                                "V"+search.get(id).getLink(), // here I set the image url
                                youtubedata ));   
                       /* new SearchTerm(
                               "image-results",// source,
                                id,
                                freq,
                                search.get(id).getThumbnail(), //here I set the thumbnail url                               
                                "V"+search.get(id).getLink(),                                
                                youtubedata ));  */       
                iterator++;
         }
        
    
    return searchTerms;
    }
    
    
    public  String getJson(String[] filter) throws IOException {

        String url =systemProperties.getProperty("youtube.uri");
         String query = "'"+StringUtils.join(filter," ")+"'"; // By Microsofts Standard Query needs to be a string
                                                             // inside single quotes

        try {
            query = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {}

        url = url+"?part=snippet&q="+query+"&maxResults=50&type=video&key="+systemProperties.getProperty("youtube.key");//&key={API_KEY}";   


     //   url = url+query;


        DefaultHttpClient httpclient = new DefaultHttpClient();
        httpclient.getCredentialsProvider().setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials(systemProperties.getProperty("youtube.key"), systemProperties.getProperty("youtube.key"))
        );
        //"https://api.datamarket.azure.com/Bing/Search/Web?$format=json&Query=%27Xbox%27"

        logger.trace("String getJson([filter]) {} ",url);

        HttpGet post = new HttpGet(url);

        HttpResponse response = httpclient.execute(post);
        
       

        return IOUtils.toString(response.getEntity().getContent());
    }

    public List<AbstractDiversifyService.Video> parseJson(String json) throws JSONException {
 
        List<AbstractDiversifyService.Video> res=new ArrayList<AbstractDiversifyService.Video>();
        ObjectMapper ob = new ObjectMapper();
        try{
            JSONObject jsonobj = new JSONObject(json);
            //JSONObject dataObject = jsonobj.getJSONObject("data");
            JSONArray items = jsonobj.getJSONArray("items");
            int i=0;
            for (int k = 0; k < 20; k++) {
                JSONObject videoObject = items.getJSONObject(k);
                JSONObject snippetObject = videoObject.getJSONObject("snippet");
                JSONObject idObject = videoObject.getJSONObject("id");
                JSONObject thumbObject = snippetObject .getJSONObject("thumbnails");
                JSONObject medthumbObject = thumbObject.getJSONObject("medium");
                String thumbUrl = medthumbObject.getString("url");
                String title = snippetObject.getString("title");
                String videoId = idObject.getString("videoId");
                String description = snippetObject.getString("description");
                AbstractDiversifyService.Video r = new AbstractDiversifyService.Video();
                String youtubeurl = "http://www.youtube.com/watch?v=";
                String videoLink = youtubeurl+videoId;

                r.setId(videoId);
                //Title
    //                    Map titleMap = (Map) ((ArrayList) o).get(2);
                //  r.setTitle((String) ((Map) o).get("Title"));
                if (title.length()>35)
                    r.setTitle((title.replace('"',' ')).substring(0, 35)+"..");
                else
                     r.setTitle(title.replace('"',' '));

                //Description
    //                    Map descriptionMap = (Map) ((ArrayList) o).get(3);
                //r.setLink((String) ((Map) o).get("MediaUrl")); //I am giving the actual url of the image

                r.setSnippet(description);

                r.setLink(videoLink);

                //  Object image = ((Map) o.get("Thumbnail"));
                r.setThumbnail( thumbUrl);


                res.add(r);


            }
        }
        catch (JSONException e) {
	            // JSON Parsing error
	            e.printStackTrace();
	}

        return res;

    }
        
    private Float getFreq(float max, Number size, Number iter){
        
        float s1 = size.floatValue();
        float s2 = iter.floatValue();
        float s3 = Float.MIN_VALUE ;
        float s4 = s1-s2 +1 ;
        float s5=s4/s1;        
        float freq = s5/5;//s5*2*max;//s5*5*max;//10*s5*max;
       // System.out.println("Image freq " + freq);
        
        return freq;
        
        
    }
    
    
    private List<String> extract(Object object) {

        if(object == null || object.toString().trim().length() <= 0){
            return new ArrayList<>();
        }

        String[] split = object.toString().split(",");
        return Arrays.asList(split);

    }

    
}
