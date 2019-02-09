package sunnn.sunsite.dao;

import org.apache.ibatis.annotations.Param;
import sunnn.sunsite.dto.CollectionBase;
import sunnn.sunsite.dto.CollectionInfo;
import sunnn.sunsite.entity.Collection;

import java.sql.Timestamp;
import java.util.List;

public interface CollectionDao {

    Collection find(long cId);

    Collection findByInfo(@Param("name") String name, @Param("group") String group);

    CollectionBase findBaseInfo(long cId);

    @Deprecated
    List<CollectionBase> findAllBaseInfoByFilter();

    List<CollectionInfo> findAllInfo(@Param("skip") int skip, @Param("limit") int limit);

    List<CollectionInfo> findAllInfoByGroup(@Param("group") String group, @Param("skip") int skip, @Param("limit") int limit);

    List<CollectionInfo> findAllInfoByType(@Param("type") String type, @Param("skip") int skip, @Param("limit") int limit);

    @Deprecated
    List<CollectionInfo> findAllInfoByFilter(@Param("skip") int skip, @Param("limit") int limit);

    void insert(Collection collection);

    void update(@Param("lastUpdate") Timestamp lastUpdate, @Param("cId") long cId);

    void updateName(@Param("cId") long cId, @Param("name") String name);

    int count();

    int countByGroup(String group);

    int countByType(String type);

    void delete(long cId);
}
