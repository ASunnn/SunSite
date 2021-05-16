package sunnn.sunsite.dao;

import org.apache.ibatis.annotations.Param;
import sunnn.sunsite.recover.Recover;
import sunnn.sunsite.recover.RecoverCollection;

import java.util.List;

public interface RecoverDao {

    void insert(Recover recover);

    List<RecoverCollection> getCollection();

    List<Recover> getPics(long cId);

    void update(@Param("sequence") long sequence, @Param("size") long size,
                    @Param("height") int height, @Param("width") int width);
}
