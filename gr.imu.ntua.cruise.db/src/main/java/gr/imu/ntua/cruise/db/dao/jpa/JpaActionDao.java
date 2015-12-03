package gr.imu.ntua.cruise.db.dao.jpa;

import com.existanze.libraries.orm.dao.JpaCommonDao;
import gr.imu.ntua.cruise.db.dao.ActionDao;
import gr.imu.ntua.cruise.db.domain.Action;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 25/09/13
 * Time: 8:12 PM
 */
@Repository("actionDao")
public class JpaActionDao extends JpaCommonDao<Action> implements ActionDao{
    protected JpaActionDao() {
        super(Action.class);
    }
}
