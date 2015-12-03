package gr.imu.ntua.cruise.db.dao;

import com.existanze.libraries.orm.dao.CommonDao;
import gr.imu.ntua.cruise.db.domain.Rating;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 25/09/13
 * Time: 5:26 PM
 */
public interface RatingDao extends CommonDao<Rating>{
    void deleteBySessionUrlSourceAndRating(String session, String url, String source, String rating);

}
