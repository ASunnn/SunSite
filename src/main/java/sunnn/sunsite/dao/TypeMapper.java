package sunnn.sunsite.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import sunnn.sunsite.entity.Type;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface TypeMapper {

    Type find(String name);

    List<Type> findAll();

    void insert(Type type);

    void update(@Param("lastUpdate") Timestamp lastUpdate, @Param("id") int id);

    void delete(String name);
}
