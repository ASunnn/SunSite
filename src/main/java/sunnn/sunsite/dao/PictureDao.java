package sunnn.sunsite.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import sunnn.sunsite.entity.Picture;

import java.util.List;

@Repository
public class PictureDao extends MongoBase<Picture> {

    public Picture findOne(String fileName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(fileName));
        return mongoTemplate.findOne(query, Picture.class);
    }

    public List<Picture> getPicture(int page, int pageSize) {
        long skip = page * pageSize;

        Query query = new Query();
        query.with(new Sort(Sort.Direction.ASC, "fileName"))
                .skip(skip).limit(pageSize);
        return mongoTemplate.find(query, Picture.class);
    }

    public long count() {
        return mongoTemplate.count(new Query(), Picture.class);
    }

}
