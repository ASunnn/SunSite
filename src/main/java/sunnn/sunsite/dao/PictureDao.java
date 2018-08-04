package sunnn.sunsite.dao;

import com.mongodb.client.result.DeleteResult;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import sunnn.sunsite.entity.Collection;
import sunnn.sunsite.entity.Illustrator;
import sunnn.sunsite.entity.Picture;

import java.util.List;

@Repository
public class PictureDao extends MongoBase<Picture> {

    public Picture findOne(int sequenceCode) {
        return findOne(
                new Query().addCriteria(Criteria.where("sequence").is(sequenceCode)),
                Picture.class);
    }

    public List<Picture> findByIllustrator(String illustratorName) {
        return find(
                new Query().addCriteria(Criteria.where("illustrator.name").is(illustratorName)),
                Picture.class);
    }

    public List<Picture> findByCollection(String collectionName) {
        return find(
                new Query().addCriteria(Criteria.where("collection.name").is(collectionName)),
                Picture.class);
    }

    public List<Picture> findFromACollection(String illustrator, String collection) {
        return find(
                new Query().addCriteria(Criteria.where("illustrator.name").is(illustrator)
                        .and("collection.name").is(collection)),
                Picture.class);
    }

    public Picture getPicture(String illustrator, String collection, String pictureName) {
        Query query = new Query();
        query.addCriteria(Criteria
                .where("illustrator.name").is(illustrator)
                .and("collection.name").is(collection)
                .and("name").is(pictureName));
        return findOne(query, Picture.class);
    }

    public List<Picture> getPictureList(int page, int pageSize) {
        long skip = page * pageSize;

        Query query = new Query();
        query.with(new Sort(Sort.Direction.ASC, "name"))
                .skip(skip).limit(pageSize);
        return find(query, Picture.class);
    }

    public long count() {
        return count(new Query(), Picture.class);
    }

    public boolean delete(int sequenceCode) {
        return remove(
                new Query().addCriteria(Criteria.where("sequence").is(sequenceCode)),
                Picture.class);
    }

}
