package sunnn.sunsite.dao;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

/**
 * MongoDB数据库操作通用方法
 *
 * @param <T> 数据库表实体
 * @author ASun
 */
public abstract class MongoBase<T> {

    @Autowired
    protected MongoTemplate mongoTemplate;

    public void insert(T object) {
        mongoTemplate.insert(object);
    }

    T findOne(Query query, Class<T> entityClass) {
        return mongoTemplate.findOne(query, entityClass);
    }

    List<T> find(Query query, Class<T> entityClass) {
        return mongoTemplate.find(query, entityClass);
    }

    List<T> findAll(Class<T> entityClass) {
        return mongoTemplate.findAll(entityClass);
    }

    long count(Query query, Class<T> entityClass) {
        return mongoTemplate.count(query, entityClass);
    }

    boolean remove(Query query, Class<T> entityClass) {
        DeleteResult result = mongoTemplate.remove(query, entityClass);
        return result.wasAcknowledged() && result.getDeletedCount() == 1;
    }

//    public UpdateResult updateFirst(Query query, Update update, Class<T> entityClass) {
//        return mongoTemplate.updateFirst(query, update, entityClass);
//    }
//
//    public UpdateResult updateMulti(Query query, Update update, Class<T> entityClass) {
//        return mongoTemplate.updateMulti(query, update, entityClass);
//    }
}
