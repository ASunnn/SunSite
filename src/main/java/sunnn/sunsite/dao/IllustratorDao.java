package sunnn.sunsite.dao;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import sunnn.sunsite.entity.Illustrator;

import java.util.List;

@Repository
public class IllustratorDao extends MongoBase<Illustrator> {

    public Illustrator findOne(String name) {
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(name));
        return mongoTemplate.findOne(query, Illustrator.class);
    }

    public List<Illustrator> getAllIllustrator() {
        return mongoTemplate.findAll(Illustrator.class);
    }

}
