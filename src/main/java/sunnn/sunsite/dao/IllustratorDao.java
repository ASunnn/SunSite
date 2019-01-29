package sunnn.sunsite.dao;

import org.apache.ibatis.annotations.Param;
import sunnn.sunsite.dto.IllustratorInfo;
import sunnn.sunsite.entity.Illustrator;
import sunnn.sunsite.entity.Pic;

import java.util.List;

public interface IllustratorDao {

    Illustrator find(String name);

    List<IllustratorInfo> findAllInfo(@Param("skip") int skip, @Param("limit") int limit);

    void insert(Illustrator illustrator);

    int count();

    void update(@Param("oldIllustrator") String oldIllustrator, @Param("newIllustrator") String newIllustrator);

    void delete(String name);

    /* ———————————— Artwork ———————————— */

    void insertAllArtwork(@Param("artworks") List artworks);

    List<Illustrator> findAllByPicture(long sequence);

    List<Pic> findAllByIllustrator(@Param("illustrator") String illustrator, @Param("skip") int skip, @Param("limit") int limit);

    int countByIllustrator(String illustrator);

    void updateArtwork(@Param("oldIllustrator") int oldIllustrator, @Param("newIllustrator") int newIllustrator);

    void deletePicture(long sequence);
}
