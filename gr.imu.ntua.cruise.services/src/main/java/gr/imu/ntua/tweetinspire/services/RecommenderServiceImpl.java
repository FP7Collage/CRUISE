package gr.imu.ntua.tweetinspire.services;

import gr.imu.ntua.cruise.lucene.CruiseAnalyzer;
import gr.imu.ntua.tweetinspire.services.bean.SearchTerm;
import gr.imu.ntua.tweetinspire.services.bean.Tweet;
import java.io.IOException;
//import gr.imu.ntua.cruise.social.UserMethods;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math.linear.OpenMapRealVector;
import org.apache.commons.math.linear.RealVectorFormat;
import org.apache.commons.math.linear.SparseRealVector;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.misc.HighFreqTerms;
import org.apache.lucene.misc.TermStats;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.NullFragmenter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.WeightedSpanTerm;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import twitter4j.IDs;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

/**
 *
 * @author imu-user
 */
public class RecommenderServiceImpl implements RecommenderService {
    
  private Logger logger = LoggerFactory.getLogger(SimpleSearchTermServiceImpl.class);   
    
    @Autowired
    TermExtractService termExtractService;
    @Autowired
    BagOfWordsService  bagOfWordsService;

    @Autowired
    private transient TwitterService twitterService;
    
   // @Autowired
   // private Twitter twitter;


//    private CruiseAnalyzer analyzer;

   @PostConstruct
    public void init(){

       
    }

        @Override    
        public List</*SearchTerm*/String> getSearchTermsForAccounts(/*float max, */String terms, String token, String tokensecret/*, String level, int resultsSize*/)  {
        
            List<SearchTerm> searchTerms = new ArrayList<>();
            HashMap<String, Integer> frequencies = new HashMap<String, Integer>();
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                .setOAuthConsumerKey("lgVvEYx0dV97iznewvQ")
                .setOAuthConsumerSecret("bu1avi4SKuQepkDzXWf6pwTMtMartMn9wcHo6TFaRU")
                .setOAuthAccessToken(token)
                .setOAuthAccessTokenSecret(tokensecret)
                .setUseSSL(true);
            

            TwitterFactory tf = new TwitterFactory(cb.build());
            
            Twitter twitter;
            
            ArrayList<String> usernames = new ArrayList<>();
            Map<User,Integer> followersnumber = new HashMap<User, Integer>();
            Map<User,Integer> followersSortedMap = new LinkedHashMap<User, Integer>();
            Map<String, List<String>> unexpectedUsers  = new HashMap<String,List<String>>();
            List<String> favouredTerms  = new ArrayList<String>();//new ArrayList<String>();
            SortedHashMap sortedHashMap = new SortedHashMap();
            //Map<String,Long> userInfo = new HashMap<String,Long>();
            Map<String,ArrayList<String>> userInfo = new HashMap<String,ArrayList<String>>();
          
            
            twitter = tf.getInstance();

            try{                         
                long cursor = -1;            
                IDs ids = twitter.getFriendsIDs(cursor);
                long[] id = ids.getIDs();
                ResponseList<User> users = twitter.lookupUsers(id);  //ResponseList<User> users = twitter.lookupUsers(srch);    
                             
                
                /**************START: Store the friends of the user in the list usernames - The search will be performed only for the accounts included in the list**************************************************************************************/
                for (User user : users) {
                    ArrayList<String> info = new ArrayList<String>();
                    info.add(Long.toString(user.getId()));
                    info.add(Integer.toString(user.getStatusesCount()));
                    info.add(Integer.toString(user.getFollowersCount()));
                     
                   // System.out.println("Total number of tweets " + user.getStatusesCount()+" for user " +user.getScreenName());
                    usernames.add('@'+user.getScreenName()); 
                    userInfo.put(user.getName(), info);
                }
                /**************END: Store the friends of the user in the list usernames - The search will be performed only for the accounts included in the list**************************************************************************************/
            }            
            catch (Exception e) {
            logger.warn("Error whilst trying to extract tweets {}",e.getLocalizedMessage());
            e.printStackTrace();
        }

        try {
                Map</*BytesRef*/String, Float> termFrequencyMap = new HashMap</*BytesRef*/String, Float>();
                Map<Long,Tweet> tweetsFor = twitterService.getTweetsFor(terms.replace(" "," OR "), usernames, 100);
                Map<String,String> usersOfTweets = new HashMap<String,String>();
                List<String> tempuserList = new ArrayList<String>();
                List<String> uniqueuserList = new ArrayList<String>();

                RAMDirectory ramDirectory = new RAMDirectory();
                CruiseAnalyzer analyzer = new CruiseAnalyzer();
                IndexWriter iw= new IndexWriter(ramDirectory,new IndexWriterConfig(Version.LUCENE_41, analyzer));

                for(Long id: tweetsFor.keySet()){

                    Tweet tweet = tweetsFor.get(id);
                  
                  //  System.out.println("Get my Retweet "+tweet.getTweet().getMyRetweet());
                  //  System.out.println("Get retweet count "+tweet.getTweet().getRetweetCount());
                                        
                    /****************************Start: Store the tweets which the Search API has returned with the users  who tweeted them ***************************************/
                    usersOfTweets.put(tweet.getText(),tweet.getFromUser());                    
                    if(!uniqueuserList.contains(tweet.getFromUser()))
                             uniqueuserList.add(tweet.getFromUser());
                    /****************************End: Store the tweets which the Search API has returned with the users  who tweeted them ***************************************/             
                                      
                    Document d = new Document();
                    d.add(new StringField("id",String.valueOf(tweet.getId()), Field.Store.YES));
                    d.add(new TextField("content",tweet.getText(), Field.Store.YES ));

                    iw.updateDocument(
                            new Term("id",String.valueOf(tweet.getId())),
                            d
                    );
                }
                iw.close();
                
                /********START: Traverse the list with the users of the tweets returned in order to form the vectors of their profiles and finally calculate their similarities with the search query***************************************************/
                UserVector[] userProfiles =new UserVector[uniqueuserList.size()];
                Map<String,Double> mapSimilarities = new HashMap<String, Double>();
                Map<String, List<String>> usersWithTheirTimelineTweetsMap  = new HashMap<String, List<String>>();
                int num=0;
                List<String> termsList = extract(terms);
                for (String u:uniqueuserList){   
                    
                       LinkedHashMap<String,Integer> bag = bagOfWordsService.getUserProfile(twitter, getUserIdFromName(userInfo,u),terms) ;
                       userProfiles[num] =  new UserVector(bag);                        
                       UserVector query = new UserVector(bag);
                       int index=0;
                       float zero=0.0f;
                       float unit = 1.0f;
                       for(String key : bag.keySet()){
                         // System.out.println("Key term "+key+" of User "+num); 
                          for (String term:termsList){
                           if(key.equals(term)){
                                query.setEntry(index,key, unit);
                           }
                           else 
                                query.setEntry(index, key, zero);
                          } 
                          float val = (float) bag.get(key);
                          userProfiles[num].setEntry(index,key, val);
                          index++;
                       }                     
                      
                       double cosineSim = similarity( userProfiles[num],query);
                       mapSimilarities.put(uniqueuserList.get(num), cosineSim);              
                       bagOfWordsService.getTweets(getUserIdFromName(userInfo,u));
                       usersWithTheirTimelineTweetsMap.put(u, bagOfWordsService.getTweets(getUserIdFromName(userInfo,u)));
                       num++;                        
                }
                /********END: Traverse the list with the users of the tweets returned in order to form the vectors of their profiles and finally calculate their similarities with the search query***************************************************/
                  
                /********START: Extract the terms from the tweets returned by Search API per user and then separate the favored ones ***************************************************/
                Map<String, List<String>> usersWithTheirTweetsMap  = new HashMap<String, List<String>>();
                Map<String, List<String>> usersWithSearchTermsList  = new HashMap<String, List<String>>();
               
                for (String key : usersOfTweets.keySet()) {
                    List<String> data = new ArrayList<String>();
                    data.add(key);
                    if (!usersWithTheirTweetsMap.containsKey(usersOfTweets.get(key))){
                            usersWithTheirTweetsMap.put(usersOfTweets.get(key), data);
                    }
                    else{
                        List<String> list = new ArrayList<>();
                        for(String s:usersWithTheirTweetsMap.get(usersOfTweets.get(key))){                        
                            list.add(s);
                        }
                        list.add(key);
                        usersWithTheirTweetsMap.put(usersOfTweets.get(key),list);
                    }              
                } 
                
                for(String key:usersWithTheirTweetsMap.keySet()){
                     List<String> tempwordslist = bagOfWordsService.getTermsFromTweets(usersWithTheirTweetsMap.get(key)) ;
                     usersWithSearchTermsList.put(key, tempwordslist);
                }                
                 for(String key:usersWithTheirTweetsMap.keySet()){
                     List<String> tempwordslist = bagOfWordsService.getTermsFromTweets(usersWithTheirTweetsMap.get(key)) ;
                     usersWithSearchTermsList.put(key, tempwordslist);
                     if (mapSimilarities.get(key)<0.15 && mapSimilarities.get(key)>0.0){
                         //Store more unexpected users with the list of their tweets
                         unexpectedUsers.put(key,usersWithTheirTimelineTweetsMap.get(key));//unexpectedUsers.put(key,usersWithTheirTweetsMap.get(key));
                         //Store user with tweets
                         favouredTerms.add("@"+key+": " +unexpectedUsers.get(key) );
                     }                     
                }               

            } catch (Exception e) {
                logger.warn("Error whilst trying to extract tweets {}",e.getLocalizedMessage());
                e.printStackTrace();
            }
            return favouredTerms;
        }
    
     private static class UserVector {
			    public LinkedHashMap<String,Integer> terms;
			    public SparseRealVector vector;
			    
			    public UserVector(LinkedHashMap<String,Integer> terms) {
			      this.terms = terms;
			      this.vector = new OpenMapRealVector(terms.size());
			    }
			    
			    //public void setEntry(String term, float freq) {
                            public void setEntry(int pos,String term, float freq) {
			      if (terms.containsKey(term)) {
			       // int pos = getPosition(term);
			       // System.out.println("Pos"+pos+"freq"+freq);
			        vector.setEntry(pos, (double) freq);			        		      }
			    } 
                            
                            public int getPosition(String item) { 
                            int index =0; 
                            for(String infoSearch:terms.keySet().toArray(new String[0])) 
                            { 
                                   index++; 
                                   if(infoSearch.equals(item)) { 
                                           return index; } 
                               } 
                            return -1; }
                            
                                                 
			    
			    public void normalize() {
			      double sum = vector.getL1Norm();
			      vector = (SparseRealVector) vector.mapDivide(sum);
			    }
			    
			    public String toString() {
			      RealVectorFormat formatter = new RealVectorFormat();
			      return formatter.format(vector);
			    }
			  }
     
     private static double similarity(UserVector u1, UserVector q)
			throws IOException

	{             
                double similarity1 = (u1.vector.dotProduct(q.vector))
				/ u1.vector.getNorm() * q.vector.getNorm();
		return similarity1;
		

	}
     
     private int getNumfFollowers(Map<String,ArrayList<String>> userInfo, String user)
     {             
            Set<String> keys = userInfo.keySet();
            int count=0;
            for (String key:keys){
                Integer value = Integer.parseInt(userInfo.get(key).get(2));          
                if (user.contains(key))
                {
                    count = value;
                }
        }
        return count; 

    }
     
      private int getSocialPowerWeight(int followersnumber)
     {             
        int count=0;
           
        return count; 

    }
     
      private int getNumOfTweets(Map<String,ArrayList<String>> userInfo, String user)
     {             
            Set<String> keys = userInfo.keySet();
            int count=0;
            for (String key:keys){
                Integer value = Integer.parseInt(userInfo.get(key).get(1));          
                if (user.contains(key))
                {
                    count = value;
                }
        }
        return count; 

    }
     
     private Long getUserIdFromName(Map<String,ArrayList<String>> map, String user){
         
        Set<String> keys = map.keySet();
        Long id=12345678910L;
        for (String key:keys){
            Long value = Long.parseLong(map.get(key).get(0));          
            if (user.contains(key))
            {
                id = value;
            }
        }
        return id;
     }
     
         private List<String> extract(Object object) {

        if(object == null || object.toString().trim().length() <= 0){
            return new ArrayList<>();
        }

        String[] split = object.toString().split(",");
        return Arrays.asList(split);

    }
     

  }