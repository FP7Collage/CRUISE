package gr.imu.ntua.cruise.db.dao.jpa;

import com.existanze.libraries.orm.dao.JpaCommonDao;
import gr.imu.ntua.cruise.db.dao.RatingDao;
import gr.imu.ntua.cruise.db.domain.Rating;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 25/09/13
 * Time: 5:26 PM
 */
@Repository("ratingDao")
public class JpaRatingDao extends JpaCommonDao<Rating> implements RatingDao {
    protected JpaRatingDao() {
        super(Rating.class);
    }

    @Override
    public void deleteBySessionUrlSourceAndRating(String session, String url, String source, String rating) {

        Query query = getEntityManager().createQuery("DELETE FROM Rating r" +
                " WHERE r.session=:session " +
                "AND r.url=:url " +
                "AND r.source=:source " +
                "AND r.rating=:rating");

        query.setParameter("session",session);
        query.setParameter("url",url);
        query.setParameter("source",source);
        query.setParameter("rating",rating);
        query.executeUpdate();

    }
}
