package sunnn.sunsite.service;

import sunnn.sunsite.dto.CollectionBase;
import sunnn.sunsite.dto.response.CollectionListResponse;
import sunnn.sunsite.exception.IllegalFileRequestException;
import sunnn.sunsite.util.StatusCode;

import java.io.File;

public interface CollectionService {

    /**
     * 创建新的画集
     *
     * @param collectionInfo 新画集信息
     * @return 处理结果
     */
    StatusCode createCollection(CollectionBase collectionInfo);

    /**
     * 获取画集列表
     *
     * @param page 页码
     * @return 画集列表
     */
    CollectionListResponse getCollectionList(int page);

    /**
     * 获取某一个社团的画集列表
     */
    CollectionListResponse getCollectionListByGroup(String group);

    /**
     * 获取一个类型的画集列表
     */
    CollectionListResponse getCollectionListByType(String type, int page);

    /**
     * 下载整个画集
     *
     * @param sequence 画集序列号
     * @return 打包好的文件
     */
    File download(long sequence) throws IllegalFileRequestException;

    /**
     * 删除一个画集
     *
     * @param sequence 画集序列号
     * @return 处理结果
     */
    StatusCode delete(long sequence);
}
