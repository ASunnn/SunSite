package sunnn.sunsite.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import sunnn.sunsite.dto.IllustratorInfo;
import sunnn.sunsite.entity.Artwork;
import sunnn.sunsite.entity.Illustrator;
import sunnn.sunsite.entity.Pic;
import sunnn.sunsite.entity.Picture;

import java.util.List;

@Mapper
public interface IllustratorMapper {

    Illustrator find(String name);

    List<IllustratorInfo> findAllInfo(@Param("skip") int skip, @Param("limit") int limit);

    void insert(Illustrator illustrator);

    int count();

    void delete(String name);

    /* ———————————— Artwork ———————————— */

    void insertAllArtwork(List list);

    List<Illustrator> findAllByPicture(long sequence);

    List<Pic> findAllByIllustrator(@Param("illustrator") String illustrator, @Param("skip") int skip, @Param("limit") int limit);

    int countByIllustrator(String illustrator);

    void update(@Param("newIllustrator") int newIllustrator, @Param("oldIllustrator") int oldIllustrator);

    void deletePicture(long sequence);
}
