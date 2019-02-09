package sunnn.sunsite.dao;

import org.apache.ibatis.annotations.Param;
import sunnn.sunsite.dto.GroupInfo;
import sunnn.sunsite.entity.Group;

import java.sql.Timestamp;
import java.util.List;

public interface GroupDao {

    Group find(String name);

    @Deprecated
    List<String> findAllBaseInfoByName(@Param("query") String query);

    List<GroupInfo> findAllInfo(@Param("skip") int skip, @Param("limit") int limit);

    List<GroupInfo> findAllInfoByName(@Param("query") String query, @Param("skip") int skip, @Param("limit") int limit);

    void insert(Group group);

    void update(@Param("lastUpdate") Timestamp lastUpdate, @Param("id") int id);

    void updateName(@Param("oldName") String oldName, @Param("newName") String newName);

    int count();

    int countByName(String query);

    void delete(String name);
}
