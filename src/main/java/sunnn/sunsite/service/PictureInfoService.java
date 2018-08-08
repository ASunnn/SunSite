package sunnn.sunsite.service;

import sunnn.sunsite.util.StatusCode;

import java.util.List;

public interface PictureInfoService {

    List getList();

    /**
     * 当传入绘师时，获取绘师关联的所有画集
     * 传入画集时，获取画集关联的所有绘
     *
     * @param name 熟悉名
     * @return 结果
     */
    List getRelatedList(String name);

    /**
     * 改变属性名
     *
     * @param oldName 原名
     * @param newName 新名
     * @return 处理结果
     */
    StatusCode changeName(String oldName, String newName);

    /**
     * 删除该属性及拥有该属性的所有图片
     *
     * @param name 属性名
     * @return 删除结果
     */
    StatusCode delete(String name);
}
