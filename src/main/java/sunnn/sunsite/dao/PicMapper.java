package sunnn.sunsite.dao;

import org.apache.ibatis.annotations.Param;
import sunnn.sunsite.entity.Pic;

import java.util.List;

public interface PicMapper {

    Pic find(long sequence);

    List<Pic> findAll(@Param("skip") int skip, @Param("limit") int limit);

    void insert(Pic picture);

    int count();

    void delete(long sequence);
}
