package gr.imu.ntua.tweetinspire.services.db;

import gr.imu.ntua.cruise.db.domain.Bookmarks;
import gr.imu.ntua.cruise.db.domain.Rating;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 22/08/13
 * Time: 1:33 PM

 */
public interface PersistanceService {

    public Bookmarks addBookmark(String url, String currentTerms, String currentQuery, String source);
    public boolean removeRating(String session, String url, String source, String rating);

    public Rating addRating(String session, String url, String term, String query, String source, String title, String theme, String rating);

    void recordQueryForSesssion(String session, String query, String theme);
    void recordTermsForSesssion(String session, String terms, String theme);
}
