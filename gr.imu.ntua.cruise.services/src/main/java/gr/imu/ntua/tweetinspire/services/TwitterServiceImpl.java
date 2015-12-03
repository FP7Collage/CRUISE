package gr.imu.ntua.tweetinspire.services;

import gr.imu.ntua.tweetinspire.services.bean.Tweet;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 9/25/12
 * Time: 4:58 PM
 */
public class TwitterServiceImpl implements TwitterService{
    private Logger logger = LoggerFactory.getLogger(TwitterServiceImpl.class);

    @Autowired
    Properties systemProperties;
    private Twitter twitter;


    @PostConstruct
    public void init(){

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(systemProperties.getProperty("twitter4j.oauth.consumerKey"))
                .setOAuthConsumerSecret(systemProperties.getProperty("twitter4j.oauth.consumerSecret"))
                .setOAuthAccessToken(systemProperties.getProperty("twitter4j.oauth.accessToken"))
                .setOAuthAccessTokenSecret(systemProperties.getProperty("twitter4j.oauth.accessTokenSecret"))
                .setDebugEnabled(systemProperties.getProperty("twitter4j.debug").equals("true"))
                .setIncludeRTsEnabled(systemProperties.getProperty("twitter4j.includeRTs").equals("true"))
                .setUseSSL(true);

        TwitterFactory tf = new TwitterFactory(cb.build());
//        tf.getInstance().setOAuthConsumer(
//                systemProperties.getProperty("twitter4j.oauth.consumerKey"),
//                systemProperties.getProperty("twitter4j.oauth.consumerSecret")
//        );
//
//        tf.getInstance().setOAuthAccessToken(new AccessToken(
//                systemProperties.getProperty("twitter4j.oauth.accessToken"),
//
//        ));

        twitter = tf.getInstance();
    }

    @Override
    public Map<Long,Tweet> getTweetsFor(boolean join, List<String> from, String... terms) throws TwitterException {


        //clean terms
        List<String> cleanTerms = new ArrayList<String>();
        for(String s : terms){
            if(!cleanTerms.contains(s.toLowerCase())){
                cleanTerms.add(s.toLowerCase());
            }
        }


        if(join){
            return this.getTweetsFor(StringUtils.join(cleanTerms," "),null,500);
        }else{

            Map<String, Map<Long,Tweet>> tweetsFetched = new HashMap<String, Map<Long, Tweet>>();

            //loop a join
            for(String t : terms){
                tweetsFetched.put(t, this.getTweetsFor(t, from, 500));
            }

            //create a single list
            Map<Long,Tweet> returnList = new HashMap<Long, Tweet>();
            for (String s : tweetsFetched.keySet()) {

                Map<Long, Tweet> longTweetMap = tweetsFetched.get(s);

                for (Long next : longTweetMap.keySet()) {
                    if (!returnList.containsKey(next)) {
                        returnList.put(next, longTweetMap.get(next));
                    }
                }
            }

            logger.trace("Map<Long,Tweet> getTweetsFor([join, terms]) Found {} tweets for terms {}"
                    ,returnList.size()
                    ,StringUtils.join(cleanTerms," "));

            return returnList;

        }

    }


    @Override
    public Map<Long,Tweet> getTweetsFor(String term, List<String> from, int howMany) throws TwitterException {

        int tries= 10;
        int pageSize =20;


        String querystr = term.trim();


        List<Query> queries = new ArrayList<>();
        if(from!=null && from.size() > 0){

            //modify the search results
            int pages = (int) Math.floor(from.size() / pageSize);
            for(int i = 0 ; i <= pages; i++){

                querystr = term.trim();

                int startIndex= i*pageSize;
                int endIndex= startIndex+pageSize;

                if(endIndex >= from.size()){
                    endIndex = from.size();
                }

                String fromStr = "";

                List<String> strings = from.subList(startIndex, endIndex);

                for(String s : strings){
                    if(s.startsWith("@")){
                        fromStr += "OR from:"+s.substring(1)+" ";
                    }
                }

                fromStr = fromStr.substring(3);

                querystr = fromStr+" "+querystr;
                Query query = new Query(querystr.trim());
                query.setCount(howMany);
                queries.add(query);

                logger.trace("Map<Long,Tweet> getTweetsFor([term, from, howMany]) Twitter query {}",query);

            }

            tries=1;
        }else{
            Query query = new Query(querystr);
            query.setCount(howMany);
            query.setLang("en");
            query.setResultType(Query.RECENT);
            queries.add(query);
        }


        List<String> tweetsProcessed=new ArrayList<String>();
        Map<Long,Tweet> tweets= new LinkedHashMap<>();
        for(Query q : queries){

            logger.trace("Map<Long,Tweet> getTweetsFor([term, from, howMany]) About to process query {}",q);

            int count =0;
            while(count <= howMany && tries > 0){

                QueryResult search = twitter.search(q);
                List<Status> tweetResult = search.getTweets();
                for (Status tweet : tweetResult) {

                    /**
                     * We want to ignore any retweets, saved tweets and same text tweets
                     */
                    if(!tweets.containsKey(tweet.getId()) && !tweetsProcessed.contains(tweet.getText().toLowerCase())){
                        tweetsProcessed.add(tweet.getText().toLowerCase());
                        tweets.put(tweet.getId(), new Tweet(tweet.getId(), tweet));
                    }
                }

                count+=tweetResult.size();
                logger.trace("Map<Long,Tweet> getTweetsFor([{}, howMany]) Current count {} ",term,tweets.size());
                tries--; //We want to get 1000 results, under 20 tries or whatever we get
            }


        }
        logger.trace("Map<Long,Tweet> getTweetsFor([terms],{}) Returning {} ", howMany,tweets.size());

        //call api
        return tweets;

    }




    public Properties getSystemProperties() {
        return systemProperties;
    }

    public void setSystemProperties(Properties systemProperties) {
        this.systemProperties = systemProperties;
    }
}
