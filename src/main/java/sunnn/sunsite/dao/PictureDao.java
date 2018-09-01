package sunnn.sunsite.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import sunnn.sunsite.dto.AggregationRes;
import sunnn.sunsite.dto.request.PictureListWithFilter;
import sunnn.sunsite.entity.Picture;
import sunnn.sunsite.dto.BaseDataBoxing;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PictureDao extends MongoBase<Picture> {

    public Picture findOne(long sequenceCode) {
        return findOne(
                Query.query(Criteria.where("sequence").is(sequenceCode)),
                Picture.class);
    }

    public List<Picture> findByIllustrator(String illustratorName) {
        return find(
                Query.query(Criteria.where("illustrator.name").is(illustratorName)),
                Picture.class);
    }

    public List<Picture> findByCollection(String collectionName) {
        return find(
                Query.query(Criteria.where("collection.name").is(collectionName)),
                Picture.class);
    }

    public List<Picture> findByPool(String illustrator, String collection) {
        return find(Query.query(Criteria.where("illustrator.name").is(illustrator)
                        .and("collection.name").is(collection)),
                Picture.class);
    }

    public List<Picture> findByPool(
            String illustrator, String collection, int page, int size, BaseDataBoxing count) {
        long skip = page * size;

        Query query = new Query();
        query.addCriteria(Criteria.where("illustrator.name").is(illustrator)
                .and("collection.name").is(collection));
        query.skip(skip).limit(size);

        count.number = count(query);
        return find(query, Picture.class);
    }

    public Picture getPicture(String illustrator, String collection, String pictureName) {
        Query query = new Query();
        query.addCriteria(Criteria
                .where("illustrator.name").is(illustrator)
                .and("collection.name").is(collection)
                .and("name").is(pictureName));
        return findOne(query, Picture.class);
    }

    public List<Picture> getPictureList(int page, int size) {
        long skip = page * size;

        Query query = new Query();
        query.with(new Sort(Sort.Direction.ASC, "name"))
                .skip(skip).limit(size);
        return find(query, Picture.class);
    }

    public List<Picture> getPictureList(PictureListWithFilter filter, BaseDataBoxing count) {
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
        count.number = count(query);
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

        if (illustrator.get(0).isEmpty()) {
            illustrator.clear();
            aggregation(criteria, "illustrator.name", illustrator);
        }
        if (collection.get(0).isEmpty()) {
            collection.clear();
            aggregation(criteria, "collection.name", collection);
        }
        if (type.get(0).isEmpty()) {
            type.clear();
            aggregation(criteria, "type.name", type);
        }
    }

    public List<String> getRelatedList(String key, String srcField, String destField) {
        Query query = new Query(Criteria.where(srcField).is(key));
        List<String> r = new ArrayList<>();
        distinct(query, destField, r);
        return r;
    }

    public Picture getFirst(String name, String type) {
        Query query = new Query(Criteria.where(type).is(name));
        return findOne(query, Picture.class);
    }

    public long count() {
        return count(new Query());
    }

    public boolean delete(long sequenceCode) {
        return remove(
                Query.query(Criteria.where("sequence").is(sequenceCode)),
                Picture.class);
    }

    public boolean deletePictures(String fields, String key) {
        Query query = new Query(Criteria.where(fields).is(key));
        return removeAll(query, count(query), Picture.class);
    }

    public boolean deletePool(String illustrator, String collection) {
        Query query = new Query(
                Criteria.where("illustrator.name").is(illustrator)
                        .and("collection.name").is(collection));
        return removeAll(query, count(query), Picture.class);
    }

    public boolean updateInfo(String field, String oldName, String newName) {
        return updateAll(Query.query(Criteria.where(field).is(oldName)),
                Update.update(field, newName),
                Picture.class);
    }

    private long count(Query query) {
        return count(query, Picture.class);
    }

    private void distinct(Query query, String field, List<String> target) {
        for (String s : mongoTemplate.getCollection("gallery")
                .distinct(field, query.getQueryObject(), String.class))
            target.add(s);
    }

    private void aggregation(Criteria criteria, String field, List<String> target) {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.project().and(field).as("fieldName"),
                Aggregation.group("fieldName")
                        .first("fieldName").as("field")
                        .count().as("count"),
                Aggregation.sort(Sort.Direction.DESC, "count")
        );

        AggregationResults<AggregationRes> results = mongoTemplate.aggregate(agg, "gallery", AggregationRes.class);
        List<AggregationRes> resList = results.getMappedResults();
        for (AggregationRes res : resList)
            target.add(res.getField());
    }
}