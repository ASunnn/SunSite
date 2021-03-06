package sunnn.sunsite.dao;

import org.apache.ibatis.annotations.Param;
import sunnn.sunsite.dto.PicInfo;
import sunnn.sunsite.entity.Picture;

import java.util.List;

public interface PictureDao {

    void insert(Picture picture);

    Picture find(long sequence);

    List<Picture> findAllByCollection(long collection);

    List<PicInfo> findAllInfoByFilter(
            @Param("type") String type, @Param("orientation") int orientation, @Param("skip") int skip, @Param("limit") int limit);

    PicInfo findInfo(long sequence);

    List<PicInfo> findAllInfoByCollection(@Param("collection") long collection, @Param("skip") int skip, @Param("limit") int limit);

    void updateCollection(@Param("sequence") long sequence, @Param("newSequence") long newSequence, @Param("collection") long collection);

    void updateName(@Param("sequence") long sequence, @Param("newSequence") long newSequence, @Param("name") String name);

    void updateIndex(@Param("index") int index, @Param("sequence") long sequence);

    int countByFilter(@Param("type") String type, @Param("orientation") int orientation);

    int countByCollection(long collection);

    void delete(long sequence);

    void deleteAllByCollection(long collection);
}
