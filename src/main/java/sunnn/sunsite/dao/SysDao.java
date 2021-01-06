package sunnn.sunsite.dao;

import org.apache.ibatis.annotations.Param;

public interface SysDao {

    void insert(@Param("version") int version, @Param("msg") String msg);

    int selectVersion();

//    String selectMsg();

    void updateVersion(@Param("version") int version, @Param("newVersion") int newVersion);

//    void updateMsgBox(@Param("version") int version, @Param("msg") String msg);
}
