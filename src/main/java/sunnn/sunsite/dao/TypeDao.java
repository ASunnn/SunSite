package sunnn.sunsite.dao;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import sunnn.sunsite.entity.Type;

import java.util.List;

@Repository
public class TypeDao extends MongoBase<Type> {

    public Type findOne(String name) {
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(name));
        return mongoTemplate.findOne(query, Type.class);
    }

    public List<Type> getAllType() {
        return mongoTemplate.findAll(Type.class);
    }
}
