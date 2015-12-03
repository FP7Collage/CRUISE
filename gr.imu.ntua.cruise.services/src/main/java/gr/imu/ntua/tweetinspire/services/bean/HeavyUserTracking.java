package gr.imu.ntua.tweetinspire.services.bean;

import gr.imu.ntua.tweetinspire.services.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import twitter4j.TwitterException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.Serializable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 30/04/13
 * Time: 4:18 PM
 */
public class HeavyUserTracking implements UserTracking,Runnable,Serializable{

    @Autowired
    private transient TwitterService twitterService;

//    @Autowired
//    private transient SessionPersistService sessionPersistService;

    @Autowired
    private transient TermExtractService termExtractService;

    @Autowired
    private Properties systemProperties;

    private transient DocumentIndexer indexer;
    //private transient NLPService nlpAnalyser;
    private Logger logger = LoggerFactory.getLogger(UserTracking.class);
    private UUID id;
    private String terms;
    private List<String> boost;
    private List<String> ignore;
    private boolean enableFrom;

    private Long lastQuery =0L;

    private Questionaire questionaire;
    private List<String> fromStrings;

    public HeavyUserTracking() {}

    public Long getSecondsToNextIndex(){
        if(lastQuery - 0L == 0 ){
            return 0L;
        }

        return ((
                Long.valueOf(systemProperties.getProperty("refresh.timer"))*1000L) - (System.currentTimeMillis()-lastQuery))/1000L;
    }

    @PreDestroy
    public void destroy() {
        logger.trace("void destroy([]) ");
        indexer.cleanUp();
        UserTrackingHandler.getInstance().remove(this);
//        try {
//            sessionPersistService.store(this);
//        } catch (Exception e) {
//            logger.error("Couldn't perist the session");
//        }
    }

    @PostConstruct
    public void init(){
        id = UUID.randomUUID();
        terms="";
        boost=new ArrayList<String>();
        ignore=new ArrayList<String>();
        questionaire=new Questionaire();
        enableFrom=false;
        lastQuery=0L;
        indexer = new DocumentIndexerImpl(id.toString(),termExtractService);
       // nlpAnalyser = new NLPServiceImpl();

    }

    public void reset(){

        logger.trace("void reset([]) Reseting {}",this.id);

        UserTrackingHandler.getInstance().remove(this);
        destroy();
        init();
        UserTrackingHandler.getInstance().submit(this);


    }

    @Override
    public List<SearchTerm> forceProcess(String terms, String[] filter, boolean from, Integer threshold) throws Exception {

        fromStrings = null;

        if(from){
            fromStrings = IOUtils.readLines(HeavyUserTracking.class.getResourceAsStream("/twitter-people.txt"));

        }   else{
            fromStrings=null;
        }

        return forceProcess(terms, filter, fromStrings, threshold);
    }

    @Override
    public List<SearchTerm> forceProcess(String terms, String[] filter, List<String> from, Integer threshold) throws Exception {

        UserTrackingHandler.getInstance().remove(this);
        destroy();
        init();

        fromStrings =from;
        this.terms = terms;
        setIgnore(filter);

        process(100, fromStrings);
        return search(terms,this.ignore,(fromStrings !=null && fromStrings.size() > 0),threshold);
    }


    private synchronized void process(int numberOfTerms, List<String> from){

        logger.trace("void process([tracking]) {} ",id);

        try {

            if(StringUtils.isEmpty(terms)){
                logger.info("Terms for session {} is empty moving on", id);
                return;
            }

            if(boost !=null && boost.size() >0){
                StringBuilder stringBuilder = new StringBuilder();
                for(String s : boost){
                    stringBuilder.append(" AND ");
                    stringBuilder.append(s);
                }

                terms +=" "+stringBuilder.toString();
            }
            logger.trace("void process([]) Processing {} ",terms);
            Map<Long, Tweet> tweetsFor = twitterService.getTweetsFor(terms, from, numberOfTerms);

            indexer.addToIndex(tweetsFor);

        } catch (TwitterException e) {
            logger.error("Exception whilst getting tweets",e);
        } finally {
        }

        lastQuery=System.currentTimeMillis();

    }

    @Override
    public List<SearchTerm> search(String terms, List<String> filter, boolean from, Integer threshold) throws Exception {
        return indexer.search(terms,filter,threshold);
    }

    @Override
    public String getId() {
        return this.id.toString();
    }

    public boolean addBoost(String s) {
        return boost.add(s);
    }

    public boolean removeBoost(String o) {
        return boost.remove(o);
    }

    public boolean removeIgnore(String o) {
        return ignore.remove(o);
    }

    public boolean addIgnore(String s) {
        return ignore.add(s);
    }

    public boolean isEnableFrom() {
        return enableFrom;
    }

    public void setEnableFrom(boolean enableFrom) {
        this.enableFrom = enableFrom;
    }

    @Override
    public String getTerms() {
        return this.terms;
    }

    @Override
    public void setTerms(String terms) {
        UserTrackingHandler.getInstance().remove(this);
        UserTrackingHandler.getInstance().submit(this);
        this.terms=terms;
    }

    @Override
    public String[] getBoosts() {
        return this.boost.toArray(new String[this.boost.size()]);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getIgnore() {
        return this.ignore.toArray(new String[this.boost.size()]);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setIgnore(String[] ignore) {

        this.ignore = new ArrayList<String>();
        Collections.addAll(this.ignore, ignore);
    }

    @Override
    public void setBoost(String[] boost) {
        this.boost = new ArrayList<String>();
        Collections.addAll(this.boost, boost);

    }


    @Override
    public Questionaire getQuestionaire() {
        return questionaire;
    }

    @Override
    public void setQuestionaire(Questionaire questionaire) {
        this.questionaire = questionaire;
    }

    @Override
    public String toString() {
        return "DefaultUserTracking{" +
                "id='"+this.id+'\''+
                "terms='" + terms + '\'' +
                '}';
    }

    @Override
    public void run() {
        process(1000,fromStrings);
    }
}
