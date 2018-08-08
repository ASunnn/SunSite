package sunnn.sunsite.dao;

import com.mongodb.client.MongoCursor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import sunnn.sunsite.dto.request.PictureListWithFilter;
import sunnn.sunsite.entity.Picture;
import sunnn.sunsite.util.BaseDataBoxing;

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

    public void getGalleryInfo(List<String> illustrator, List<String> collection, List<String> type) {
        Criteria criteria = new Criteria();
        if (!type.get(0).isEmpty())
            criteria.and("type.name").is(type.get(0));
        if (!illustrator.get(0).isEmpty())
            criteria.and("illustrator.name").is(illustrator.get(0));
        if (!collection.get(0).isEmpty())
            criteria.and("collection.name").is(collection.get(0));
        Query query = new Query(criteria);

        if (illustrator.get(0).isEmpty()) {
            MongoCursor<String> result = mongoTemplate.getCollection("gallery")
                    .distinct("illustrator.name", query.getQueryObject(), String.class)
                    .iterator();
            illustrator.clear();
            while (result.hasNext())
                illustrator.add(result.next());
        }
        if (collection.get(0).isEmpty()) {
            MongoCursor<String> result = mongoTemplate.getCollection("gallery")
                    .distinct("collection.name", query.getQueryObject(), String.class)
                    .iterator();
            collection.clear();
            while (result.hasNext())
                collection.add(result.next());
        }
        if (type.get(0).isEmpty()) {
            MongoCursor<String> result = mongoTemplate.getCollection("gallery")
                    .distinct("type.name", query.getQueryObject(), String.class)
                    .iterator();
            type.clear();
            while (result.hasNext())
                type.add(result.next());
        }
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