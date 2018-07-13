package sunnn.sunsite.dao;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import sunnn.sunsite.entity.Picture;

@Repository
public class PictureDao extends MongoBase<Picture> {

    public Picture findOne(String fileName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(fileName));
        return mongoTemplate.findOne(query, Picture.class);
    }

}
