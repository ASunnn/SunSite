package sunnn.sunsite.service;

import sunnn.sunsite.exception.IllegalFileRequestException;
import sunnn.sunsite.util.StatusCode;

import java.io.File;
import java.util.List;

public interface PictureInfoService {

    List getList();

    /**
     * 当传入绘师时，获取绘师关联的所有画集
     * 传入画集时，获取画集关联的所有绘师
     *
     * @param name 属性名
     * @return 结果
     */
    List<String> getRelatedList(String name);

    /**
     * 下载该绘师/画集的所有图片
     *
     * @param name 绘师名
     * @return 文件表示
     * @throws IllegalFileRequestException  请求了非法的文件
     */
    File download(String name) throws IllegalFileRequestException;

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

    /**
     * 获取该属性的封面图
     *
     * @param name 属性名
     * @return 图片序列号
     */
    long getThumbnailSequence(String name);
}
