package sunnn.sunsite.dao;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

/**
 * MongoDB数据库操作接口
 * @author ASun
 * @param <T>   数据库表实体
 */
public interface MongoBase<T> {

    /**
     * 查询记录数
     * @param query 查询条件
     * @param entityClass   实体类
     * @return  查询结果
     */
    long count(Query query, Class<T> entityClass);

    /**
     * 插入
     * @param object    需要插入的实体
     */
    void insert(T object);

    /**
     * 查找一个结果
     * @param query 查询条件
     * @param entityClass   实体类
     * @return  查询结果
     */
    T findOne(Query query, Class<T> entityClass);

    /**
     * 返回符合查询的所有结果
     * @param query 查询条件
     * @param entityClass   实体类
     * @return  查询结果集
     */
    List<T> find(Query query, Class<T> entityClass);

    /**
     * 返回表的所有记录
     * @param entityClass   实体类
     * @return  结果集
     */
    List<T> findAll(Class<T> entityClass);

    void updateFirst(Query query, Update update, Class<T> entityClass);

    void updateMulti(Query query, Update update, Class<T> entityClass);

    /**
     * 移除记录
     * @param query 查询条件
     * @param entityClass   实体类
     */
    void remove(Query query, Class<T> entityClass);

}
