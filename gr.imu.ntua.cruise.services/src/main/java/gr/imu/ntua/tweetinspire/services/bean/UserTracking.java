package gr.imu.ntua.tweetinspire.services.bean;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 30/04/13
 * Time: 2:42 PM
 */
public interface UserTracking {


    String getTerms();
    String[] getBoosts();
    String[] getIgnore();

    String getId();
    void setIgnore(String[] ignore);
    void setBoost(String[] boost);
    void setTerms(String terms);

    List<SearchTerm> search(String terms, List<String> filter, boolean getFrom, Integer threshold) throws Exception;

    Long getSecondsToNextIndex();

    void destroy();
    void reset();

    void setQuestionaire(Questionaire questionaire);

    Questionaire getQuestionaire();

    List<SearchTerm> forceProcess(String terms, String[] filter, boolean getFrom , Integer threshold) throws Exception;

    List<SearchTerm> forceProcess(String terms, String[] filter, List<String> from, Integer threshold) throws Exception;
}
