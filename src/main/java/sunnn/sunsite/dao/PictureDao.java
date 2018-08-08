package sunnn.sunsite.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import sunnn.sunsite.dto.request.PictureListWithFilter;
import sunnn.sunsite.entity.Illustrator;
import sunnn.sunsite.entity.Picture;
import sunnn.sunsite.util.BaseDataBoxing;

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

    public List<Picture> findFromOneCollection(String illustrator, String collection) {
        return find(
                Query.query(Criteria.where("illustrator.name").is(illustrator)
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
            illustrator.clear();
            distinct(query, "illustrator.name", illustrator);
        }
        if (collection.get(0).isEmpty()) {
            collection.clear();
            distinct(query, "collection.name", illustrator);
        }
        if (type.get(0).isEmpty()) {
            type.clear();
            distinct(query, "type.name", illustrator);
        }
    }

    public List<String> getRelatedList(String key, String srcField, String destField) {
        Query query = new Query(Criteria.where(srcField).is(key));
        List<String> r = new ArrayList<>();
        distinct(query, destField, r);
        return r;
    }

    private void distinct(Query query, String field, List<String> result) {
        for (String s : mongoTemplate.getCollection("gallery")
                .distinct(field, query.getQueryObject(), String.class))
            result.add(s);
    }

    public long count() {
        return count(new Query());
    }

    private long count(Query query) {
        return count(query, Picture.class);
    }

    public boolean delete(long sequenceCode) {
        return remove(
                Query.query(Criteria.where("sequence").is(sequenceCode)),
                Picture.class);
    }

    public boolean deleteIllustrator(String illustratorName) {
        Query query = new Query(Criteria.where("illustrator.name").is(illustratorName));
        return removeAll(query, count(query), Picture.class);
    }

    public boolean updateIllustrator(String oldName, String newName) {
        return updateAll(Query.query(Criteria.where("illustrator.name").is(oldName)),
                Update.update("illustrator.name", newName),
                Picture.class);
    }
}