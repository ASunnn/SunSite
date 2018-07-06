package sunnn.sunsite.dao;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

public abstract class MongoBaseImpl<T> implements MongoBase<T> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public long count(Query query,Class<T> entityClass) {
        return mongoTemplate.count(query,entityClass);
    }

    public void insert(T object) {
        mongoTemplate.insert(object);
    }

    public T findOne(Query query, Class<T> entityClass) {
        return mongoTemplate.findOne(query,entityClass);
    }

    public List<T> find(Query query, Class<T> entityClass) {
        return mongoTemplate.find(query,entityClass);
    }

    public List<T> findAll(Class<T> entityClass) {
        return mongoTemplate.findAll(entityClass);
    }

    public void updateFirst(Query query, Update update, Class<T> entityClass) {
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, entityClass);
        System.out.println(updateResult.toString());
    }

    public void updateMulti(Query query, Update update, Class<T> entityClass) {
        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, entityClass);
        System.out.println(updateResult.toString());
    }

    public void remove(Query query, Class<T> entityClass) {
        DeleteResult remove = mongoTemplate.remove(query, entityClass);
        System.out.println(remove.toString());
    }
}
