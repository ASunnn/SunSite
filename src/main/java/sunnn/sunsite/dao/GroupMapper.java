package sunnn.sunsite.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import sunnn.sunsite.dto.GroupInfo;
import sunnn.sunsite.entity.Group;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface GroupMapper {

    Group find(String name);

    List<GroupInfo> findAllInfo(@Param("skip") int skip, @Param("limit") int limit);

    void insert(Group group);

    void update(@Param("lastUpdate") Timestamp lastUpdate, @Param("id") int id);

    int count();

    void delete(String name);
}
