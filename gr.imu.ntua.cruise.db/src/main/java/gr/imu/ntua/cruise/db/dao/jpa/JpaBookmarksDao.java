package gr.imu.ntua.cruise.db.dao.jpa;

import com.existanze.libraries.orm.dao.JpaCommonDao;
import gr.imu.ntua.cruise.db.dao.BookmarksDao;
import gr.imu.ntua.cruise.db.domain.Bookmarks;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 22/08/13
 * Time: 1:27 PM
 */
@Repository("bookmarksDao")
public class JpaBookmarksDao extends JpaCommonDao<Bookmarks> implements BookmarksDao{
    protected JpaBookmarksDao() {
        super(Bookmarks.class);
    }
}
