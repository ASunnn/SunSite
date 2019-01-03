package sunnn.sunsite.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import sunnn.sunsite.dto.PictureBase;
import sunnn.sunsite.entity.Picture;

import java.util.List;

public interface PictureMapper {

    void insert(Picture picture);

    Picture find(long sequence);

    List<Picture> findAllByCollection(long collection);

    PictureBase findBaseInfo(long sequence);

    List<PictureBase> findAllBaseInfoByCollection(@Param("collection") long collection, @Param("skip") int skip, @Param("limit") int limit);

    void updateIndex(@Param("index") int index, @Param("sequence") long sequence);

    int countByCollection(long collection);

    void delete(long sequence);

    void deleteAllByCollection(long collection);
}
