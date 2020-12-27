package sunnn.sunsite.dao;

import org.apache.ibatis.annotations.Param;
import sunnn.sunsite.dto.TypeInfo;
import sunnn.sunsite.entity.Type;

import java.sql.Timestamp;
import java.util.List;

public interface TypeDao {

    Type find(String name);

    TypeInfo findInfo(String name);

    List<TypeInfo> findAllInfo();

    void insert(Type type);

    void update(@Param("lastUpdate") Timestamp lastUpdate, @Param("id") int id);

    void delete(String name);
}
