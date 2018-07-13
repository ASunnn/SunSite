package sunnn.sunsite.dao;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import sunnn.sunsite.entity.Collection;

@Repository
public class CollectionDao extends MongoBase<Collection> {

    public Collection findOne(String collectionName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(collectionName));
        return mongoTemplate.findOne(query, Collection.class);
    }
}