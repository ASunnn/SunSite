package sunnn.sunsite.dao;

import org.apache.ibatis.annotations.Param;
import sunnn.sunsite.entity.Alias;

import java.util.List;

public interface AliasDao {

    void insertAllAlias(@Param("alias") List aliases);

    void deleteAlias(Alias alias);

    List<String> getAllAliasByOrigin(@Param("origin") int origin, @Param("kind") int kind);
}
