package sunnn.sunsite.service;

import sunnn.sunsite.entity.Alias;
import sunnn.sunsite.util.StatusCode;

public interface AliasService {

    /**
     * 获取别名
     *
     * @param origin 正式名
     * @param kind   类型
     * @return 别名列表
     */
    String[] getAlias(int origin, int kind);

    /**
     * 修改别名
     *
     * @param origin  正式名
     * @param kind    类型
     * @param aliases 别名列表
     * @return 修改结果
     */
    StatusCode modifyAlias(int origin, int kind, String[] aliases);

    /**
     * 删除别名
     *
     * @param alias 别名
     * @return 删除结果
     */
    StatusCode deleteAlias(Alias alias);
}
