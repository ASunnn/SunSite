package sunnn.sunsite.dao;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import sunnn.sunsite.entity.Collection;
import sunnn.sunsite.entity.Illustrator;
import sunnn.sunsite.entity.Picture;

import java.util.List;

@Repository
public class CollectionDao extends MongoBase<Collection> {

    public Collection findOne(String collectionName) {
        return findOne(
                Query.query(Criteria.where("name").is(collectionName)),
                Collection.class);
    }

    public List<Collection> findByType(String type) {
        return find(
                Query.query(Criteria.where("type.name").is(type)),
                Collection.class);
    }

    public List<Collection> getAllCollection() {
        return findAll(Collection.class);
    }

    public boolean delete(String name) {
        return remove(
                Query.query(Criteria.where("name").is(name)),
                Collection.class);
    }

    public boolean updateCollection(String oldName, String newName) {
        return updateOne(
                Query.query(Criteria.where("name").is(oldName)),
                Update.update("name", newName),
                Collection.class);
    }

}