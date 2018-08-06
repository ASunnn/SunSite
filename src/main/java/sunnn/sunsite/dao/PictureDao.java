package sunnn.sunsite.dao;

import com.mongodb.DBObject;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCursor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import sunnn.sunsite.dto.request.PictureListWithFilter;
import sunnn.sunsite.entity.Picture;
import sunnn.sunsite.util.BaseDataBoxing;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PictureDao extends MongoBase<Picture> {

    public Picture findOne(long sequenceCode) {
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

    public List<Picture> findFromOneCollection(String illustrator, String collection) {
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

    public List<Picture> getPictureList(PictureListWithFilter filter, BaseDataBoxing dataCount) {
        int limit = filter.getSize();
        long skip = limit * filter.getPage();

        Criteria criteria = new Criteria();
        if (!filter.getType().isEmpty())
            criteria.and("type.name").is(filter.getType());

        if (!filter.getCollection().isEmpty())
            criteria.and("collection.name").is(filter.getCollection());

        if (!filter.getIllustrator().isEmpty())
            criteria.and("illustrator.name").is(filter.getIllustrator());

        criteria.and("name").regex(".*" + filter.getName() + ".*");

        Query query = new Query(criteria);
        query.with(new Sort(Sort.Direction.ASC, "name"))
                .skip(skip).limit(limit);
        dataCount.number = count(query);
        return find(query, Picture.class);
    }

    public List<String> getGalleryInfo(String type, String illustrator, String collection, String field) {
        Criteria criteria = new Criteria();
        if (!type.isEmpty())
            criteria.and("type.name").is(type);
        if (!illustrator.isEmpty())
            criteria.and("illustrator.name").is(illustrator);
        if (!collection.isEmpty())
            criteria.and("collection.name").is(collection);
        Query query = new Query(criteria);

        MongoCursor<String> result = mongoTemplate.getCollection("gallery")
                .distinct(field, query.getQueryObject(), String.class)
                .iterator();

        List<String> r = new ArrayList<>();
        while (result.hasNext())
            r.add(result.next());
        return r;
    }

    public long count() {
        return count(new Query());
    }

    private long count(Query query) {
        return count(query, Picture.class);
    }

    public boolean delete(long sequenceCode) {
        return remove(
                new Query().addCriteria(Criteria.where("sequence").is(sequenceCode)),
                Picture.class);
    }
}