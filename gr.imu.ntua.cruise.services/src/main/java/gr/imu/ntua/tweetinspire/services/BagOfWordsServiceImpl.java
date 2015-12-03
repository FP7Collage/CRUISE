/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gr.imu.ntua.tweetinspire.services;

import gr.imu.ntua.cruise.lucene.CruiseAnalyzer;
import gr.imu.ntua.cruise.lucene.CruiseAnalyzer.CustomFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import org.apache.commons.io.IOUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 *
 * @author maria
 */
public class BagOfWordsServiceImpl implements BagOfWordsService {
    
      private Logger logger = LoggerFactory.getLogger(AbstractDiversifyService.class);
      private  List<String> strings=null; 
      private Map<Long,ArrayList<String>> tweets=new HashMap<Long,ArrayList<String>>();
      
      
      
    @PostConstruct
    public void init(){
       
                    try{
                        strings = IOUtils.readLines(
                                new InputStreamReader(
                                        this.getClass().getResourceAsStream("/stopwords.txt"),
                                        "UTF-8"));
                    }catch(Exception e){
                        logger.warn("Couldn't read the stop words {}",e);
                    }
    } 
    
    @Override
    public LinkedHashMap<String,Integer> getUserProfile(Twitter twitter, Long from_user, String exploreterms)  throws TwitterException{
    
                   LinkedHashMap<String, Integer> wordfrequencyMap = new LinkedHashMap<>(); 
                   List<String> bagOfWords = new ArrayList<>();
                   List<String> tempList = new ArrayList<>();
                   List<String> explorelist = new ArrayList<>();
                   ArrayList<String> tweettempList = new ArrayList<>();
                   Long tweetsinceId=0L;
                   explorelist = extract(exploreterms);                  
                   Paging paging;
                   DateFormat dateformat =  new SimpleDateFormat("EEE MMM d yyyy HH:mm:ss z");
                   Date date =  new Date();
                   Date dateBefore = new Date(date.getTime()-7*24*3600*1000);
                   
                  
                   for(int i=1;i<6;i++){
                            if (tweetsinceId==0L){
                                 paging = new Paging(i, 100); 
                            }
                            else{
                             paging = new Paging(i, 100).maxId(tweetsinceId);
                            }
                            if(twitter.getUserTimeline(from_user,paging)!=null){
                                List<Status> statusess = twitter.getUserTimeline(from_user,paging);
                                Date firstDate = twitter.getUserTimeline(from_user,paging).get(0).getCreatedAt();
                                if  (dateformat.format(dateBefore).compareTo(dateformat.format(firstDate))<=0)
                                    continue;
                                System.out.println("Page " +i +" date of first tweet " +firstDate+ " date of last tweet ");
                                for (Status status3 : statusess)
                                {
                                    try {
                                        
                                        String tweet = status3.getText();
                                        Tokenizer tokenStream = new ClassicTokenizer(Version.LUCENE_41, new StringReader(tweet));
                                        //Tokenstream tokenStream = new ClassicTokenizer(Version.LUCENE_41, new StringReader(tweet));
                                        // remove stop words
                                        LowerCaseFilter filter = new LowerCaseFilter(Version.LUCENE_41, tokenStream);
                                        
                                        //StandardFilter filter1 = new StandardFilter(Version.LUCENE_41, filter);
                                        // tokenStream = new StopFilter(Version.LUCENE_41, tokenStream, new CharArraySet(Version.LUCENE_41,strings, true));
                                        StopFilter stopFilter = new StopFilter(Version.LUCENE_41, tokenStream, new CharArraySet(Version.LUCENE_41,strings, true));
                                        
                                        CruiseAnalyzer cruisean = new CruiseAnalyzer();
                                        CruiseAnalyzer.CustomFilter  filter3 = cruisean.new CustomFilter(stopFilter);//new CruiseAnalyzer.CustomFilter(stopFilter);
                                        Analyzer.TokenStreamComponents  tokenStreamComp =  new Analyzer.TokenStreamComponents(tokenStream, filter3);
                                        // retrieve the remaining tokens
                                        
                                        //Set<String> tokens = new HashSet<String>();
                                        TokenStream tokenStream2 = tokenStreamComp.getTokenStream();
                                        OffsetAttribute offsetAttribute = tokenStream2.addAttribute(OffsetAttribute.class);
                                        CharTermAttribute charTermAttribute = tokenStream2.addAttribute(CharTermAttribute.class);
                                        tokenStream2.reset();
                                        while (tokenStream2.incrementToken()) {
                                            int startOffset = offsetAttribute.startOffset();
                                            int endOffset = offsetAttribute.endOffset();
                                            String term = charTermAttribute.toString();
                                            bagOfWords.add(term.toLowerCase()); // Store for user profiling
                                            for (String s : explorelist){
                                                if (term.toLowerCase().equals(s))
                                                    tweettempList.add(tweet);
                                            }
                                        }
                                        tweetsinceId = status3.getId();
                                        
                                    } catch (IOException ex) {
                                        java.util.logging.Logger.getLogger(BagOfWordsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }                            
                   }
                   
                   tweets.put(from_user, tweettempList);
                   for(String s:bagOfWords ){                       
                       if(!tempList.contains(s)){                          
                           wordfrequencyMap.put(s, Collections.frequency(bagOfWords, s));
                           tempList.add(s);                                                 
                       }        
                   } 
                   
                  return wordfrequencyMap;
     }
    
    
    @Override
    public List<String> getTweets(Long twitteruser){
        
        return tweets.get(twitteruser);
        
    }
    
    @Override 
    public List<String> getTermsFromTweets(List<String> listOfTweets){
    
        List<String> wordsOfTweets = new ArrayList<String>();
        for (String tweet:listOfTweets){
            try {                          
                           // tokenize the input tweet
                           TokenStream tokenStream = new ClassicTokenizer(Version.LUCENE_41, new StringReader(tweet));
                           // remove stop words
                           tokenStream = new StopFilter(Version.LUCENE_41, tokenStream, new CharArraySet(Version.LUCENE_41,strings, true));                           
                           // retrieve the remaining tokens
                           //Set<String> tokens = new HashSet<String>();
                           OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
                           CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
                           tokenStream.reset();
                           while (tokenStream.incrementToken()) {
                               int startOffset = offsetAttribute.startOffset();
                               int endOffset = offsetAttribute.endOffset();
                               String term = charTermAttribute.toString();
                               wordsOfTweets.add(term.toLowerCase());
                           }                   
                       } catch (IOException ex) {
                           java.util.logging.Logger.getLogger(BagOfWordsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                       }
        }
     return wordsOfTweets;
    }
  private List<String> extract(Object object) {

        if(object == null || object.toString().trim().length() <= 0){
            return new ArrayList<>();
        }

        String[] split = object.toString().split(",");
        return Arrays.asList(split);

    }
     
}
