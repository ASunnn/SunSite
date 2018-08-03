package sunnn.sunsite.dao;

import org.springframework.stereotype.Repository;
import sunnn.sunsite.entity.Me;

/**
 * Me实体类的持久层
 *
 * @author ASun
 */
@Repository
public class MeDao extends MongoBase<Me> {

    public Me getCorrectMe() {
        return findAll(Me.class).get(0);
    }

}
