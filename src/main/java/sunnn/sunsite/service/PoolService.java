package sunnn.sunsite.service;

import sunnn.sunsite.dto.response.PictureListResponse;
import sunnn.sunsite.entity.Picture;
import sunnn.sunsite.exception.IllegalFileRequestException;
import sunnn.sunsite.util.StatusCode;

import java.io.File;
import java.util.List;

public interface PoolService {

    /**
     * 从指定的pool中获取所有图片
     *
     * @param illustrator 绘师
     * @param collection  画集
     * @return 查找出的图片
     */
    List<Picture> getPictureFromPool(String illustrator, String collection);

    /**
     * 从指定的pool中获取图片文件分页结果
     *
     * @param illustrator 绘师
     * @param collection  画集
     * @param page        页码
     * @param size        每页数据量
     * @return 查找出的图片分页结果
     */
    PictureListResponse getPictureFromPool(String illustrator, String collection, int page, int size);

    /**
     * 打包下载一个pool
     *
     * @param illustratorName 绘师
     * @param collectionName  画集
     * @return 下载文件
     * @throws IllegalFileRequestException 请求了非法的文件
     */
    File download(String illustratorName, String collectionName) throws IllegalFileRequestException;

    /**
     * 删除一个pool
     *
     * @param illustratorName 绘师
     * @param collectionName  画集
     * @return 删除结果
     */
    StatusCode delete(String illustratorName, String collectionName);

}
