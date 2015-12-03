/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gr.imu.ntua.tweetinspire.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author maria
 */
public class SocialRecommenderServiceImpl implements SocialRecommenderService {
    
       @Autowired
   RecommenderService recommenderService;
    
    
  public   List<String> getRecommendationsFor(String terms, String usertoken, String usersecret){
    
    
      List<String> diversified = new ArrayList<String>();
      
      diversified = recommenderService.getSearchTermsForAccounts(terms, usertoken, usersecret);
//    HttpPost post = new HttpPost(systemProperties.getProperty("diversify.url"));
//    post.setEntity(
//            new StringEntity(json, ContentType.APPLICATION_JSON)
//    );
//
//    int connectionTimeout =
//            Integer.valueOf(systemProperties.getProperty("http.connectionTimeout"));
//
//    HttpParams connectionParameters = new BasicHttpParams();
//    HttpConnectionParams.setConnectionTimeout(connectionParameters,connectionTimeout);
//    HttpConnectionParams.setSoTimeout(connectionParameters,connectionTimeout);
//
//    HttpClient c = new DefaultHttpClient(connectionParameters);
//
//    long start = System.currentTimeMillis();
//    HttpResponse execute = c.execute(post);
//    String resultIds = IOUtils.toString(execute.getEntity().getContent());
//
//    long time = System.currentTimeMillis() - start;
//    logger.debug("List<Result> diversify([resultList]) Diversification took {} ms \n\t{}",time,resultIds);
//    List idsList = objectMapper.readValue(resultIds, List.class);
//    List<AbstractDiversifyService.Result> diversified = new ArrayList<AbstractDiversifyService.Result>();
//
//    for(Object object: idsList){
//
//        Integer id1 = (Integer) ((Map) object).get("id");
//        AbstractDiversifyService.Result id = resultList.get(id1);
//        diversified.add(id);
//    }
//
        return diversified;
    
}
}
