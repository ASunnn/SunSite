package sunnn.sunsite.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;
import sunnn.sunsite.entity.mongo.Me;

/**
 * Me实体类的持久层
 *
 * @author ASun
 */
@Repository
public class MeDao {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public MeDao(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Me getCorrectMe() {
        return mongoTemplate.findAll(Me.class).get(0);
    }

}
