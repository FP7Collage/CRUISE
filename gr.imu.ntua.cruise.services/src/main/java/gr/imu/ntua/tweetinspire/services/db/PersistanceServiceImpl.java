package gr.imu.ntua.tweetinspire.services.db;

import gr.imu.ntua.cruise.db.dao.ActionDao;
import gr.imu.ntua.cruise.db.dao.BookmarksDao;
import gr.imu.ntua.cruise.db.dao.RatingDao;
import gr.imu.ntua.cruise.db.domain.Action;
import gr.imu.ntua.cruise.db.domain.Bookmarks;
import gr.imu.ntua.cruise.db.domain.Rating;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 22/08/13
 * Time: 1:34 PM
 */
public class PersistanceServiceImpl implements PersistanceService {

    private Logger logger = LoggerFactory.getLogger(PersistanceServiceImpl.class);


    @Autowired
    BookmarksDao bookmarksDao;

    @Autowired
    RatingDao ratingDao;

    @Autowired
    ActionDao actionDao;

    @Override
    @Transactional
    public Bookmarks addBookmark(String url, String currentTerms, String currentQuery,String source) {


        Bookmarks b = new Bookmarks();
        b.setUrl(url);
        b.setTerms(currentTerms);
        b.setQuery(currentQuery);
        b.setSource(source);

        return bookmarksDao.insert(b);

    }


    @Override
    @Transactional
    public Rating addRating(String session, String url, String term, String query, String source, String title, String theme,  String rating) {

        Rating r = new Rating();
        r.setSession(session);
        r.setUrl(url);
        r.setTerms(term);
        r.setTitle(title);
        r.setTheme(theme);
        r.setQuery(query);
        r.setSource(source);
        r.setRating(rating);

        return ratingDao.insert(r);  //To change body of implemented methods use File | Settings | File Templates.

    }

    @Override
    @Transactional
    public boolean removeRating(String session, String url, String source, String rating) {


        boolean ret = false;

        try{

            ratingDao.deleteBySessionUrlSourceAndRating(
                session,
                url,
                source,
                rating);
            ret =true;

        }catch (Exception ignore){
            logger.warn("Couldn't delete rating",ignore);
        }

        return ret;
    }

    @Override
    @Transactional
    public void recordTermsForSesssion(String session, String terms, String theme) {

        try{
            Action a = new Action();
            a.setSession(session);
            a.setTheme(theme);
            a.setContent(terms);
            a.setAction("terms-search");
            actionDao.insert(a);
        }catch (Exception e){
            logger.debug("Couldn't insert the action");
        }

    }

    @Override
    @Transactional
    public void recordQueryForSesssion(String session, String query, String theme) {

        try{
            Action a = new Action();
            a.setSession(session);
            a.setTheme(theme);
            a.setContent(query);
            a.setAction("query-expansion");
            actionDao.insert(a);

        }catch (Exception e){
            logger.debug("Couldn't insert the action");
        }

    }
}
