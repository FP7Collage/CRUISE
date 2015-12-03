/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gr.imu.ntua.tweetinspire.services;

import gr.imu.ntua.tweetinspire.services.bean.SearchTerm;
import gr.imu.ntua.tweetinspire.services.bean.UserTracking;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import twitter4j.Twitter;

/**
 *
 * @author maria
 */
public class AllResultsIntegrationServiceImpl implements AllResultsIntegrationService{
    
      private Logger logger = LoggerFactory.getLogger(AbstractDiversifyService.class);
      private  List<String> strings=null;
      


    @Autowired
    DiversifyService diversifyService;

    @Autowired
    UserTracking userTracking;


    @Autowired
    SimpleSearchTermService simpleSearchTermService;


    
    @Autowired
    ImagesService imagesService;
     
    @Autowired
    PersonalSearchTermService personalSearchTermService;
    
    @Autowired
    YouTubeService youTubeService;
    
    
    List<String> acl;
    String low ;
    String medium;
    String deep ;
    String publiconly;
    String personal;
    String bothstreams;
      
      
    @PostConstruct
    public void init(){
        acl=new ArrayList<>();
        low = "low";
        medium = "medium" ;
        deep = "deep";
        publiconly = "public";
        personal = "personal";
        bothstreams = "both";    
    }
    
    @Override
    public List<SearchTerm> getAllResults(String terms, String level, String source, String userId, String userSecret){
     List<String> termsList = extract(terms);
        List<SearchTerm> results = new ArrayList<SearchTerm>();
        
        int resultsSize = 0;

        if(termsList.size() >0 ){

            try {
                  
                float cealing = 1.0f;
              /*  if(full.size() > 0 ){
                    cealing= full.get(0).getFrequency();
                }*/
                
              if(source.equals(bothstreams) ||  source.equals(publiconly)){
              
                List<SearchTerm> full = userTracking.forceProcess(
                        StringUtils.join(termsList," "),
                        new String[]{},
                        false,
                        0
                ) ;
                Collections.addAll(results,full.toArray(new SearchTerm[full.size()])); 
                resultsSize = results.size();
                List<SearchTerm> images = imagesService.getImagesResults(cealing, terms,resultsSize);
                Collections.addAll(results,images.toArray(new SearchTerm[images.size()]));  
                resultsSize = results.size();
              }
              
              else if (source.equals(personal)){
                  List<SearchTerm> images = imagesService.getImagesResults(cealing, terms,resultsSize);
                  Collections.addAll(results,images.toArray(new SearchTerm[images.size()]));  
                  resultsSize = results.size();
                  List<SearchTerm> myfollowers = personalSearchTermService.getSearchTermsForAccounts(
                            cealing ,
                            terms,
                            userId,//UserMethods.getCurrentUser().get(0),
                            userSecret,//UserMethods.getCurrentUser().get(1) ,
                            level,
                            resultsSize

                    );
                  Collections.addAll(results,myfollowers.toArray(new SearchTerm[myfollowers.size()]));
                  resultsSize = results.size();
              
              }                           
                                          
               
                if (("medium").equals(level)) {
                    
                     List<SearchTerm> binglinks = diversifyService.getLinksAsTerms(cealing, terms, resultsSize);
                     Collections.addAll(results,binglinks.toArray(new SearchTerm[binglinks.size()]));
                     resultsSize = results.size();
                   
                }
                
                if (("deep").equals(level)) {
                    
                      List<SearchTerm> binglinks = diversifyService.getLinksAsTerms(cealing, terms,resultsSize);
                      Collections.addAll(results,binglinks.toArray(new SearchTerm[binglinks.size()]));  
                      List<SearchTerm> videolinks = youTubeService.getVideosFor(cealing, terms, resultsSize);
                      Collections.addAll(results,videolinks.toArray(new SearchTerm[videolinks.size()]));
                      resultsSize = results.size();
                }
                        
                        
                if(source.equals(bothstreams)&& (userId!=null && !userId.isEmpty()) && (userSecret!=null && !userSecret.isEmpty()) ){ //&& UserMethods.getCurrentUser()!=null ){
                    
                    resultsSize = results.size();
                     List<SearchTerm> myfollowers = personalSearchTermService.getSearchTermsForAccounts(
                            cealing ,
                            terms,
                            userId,//UserMethods.getCurrentUser().get(0),
                            userSecret,//UserMethods.getCurrentUser().get(1) ,
                            level,
                            resultsSize

                    );
                   //resultsSize = results.size();
                    Collections.addAll(results,myfollowers.toArray(new SearchTerm[myfollowers.size()]));
                    resultsSize = results.size();
                }
                
                          
                

            } catch (Exception e) {
                logger.warn("Couldn't fetch cloud",e);
            }
             
        }
        return results;
        
    }
    
    private List<String> extract(Object object) {

        if(object == null || object.toString().trim().length() <= 0){
            return new ArrayList<>();
        }

        String[] split = object.toString().split(",");
        return Arrays.asList(split);

    }
}
