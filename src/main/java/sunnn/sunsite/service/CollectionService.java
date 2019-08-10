package sunnn.sunsite.service;

import sunnn.sunsite.dto.request.UploadCollectionInfo;
import sunnn.sunsite.dto.response.CollectionInfoResponse;
import sunnn.sunsite.dto.response.CollectionListResponse;
import sunnn.sunsite.dto.response.ModifyResultResponse;
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
    StatusCode createCollection(UploadCollectionInfo collectionInfo);

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
     * 获取一个画集的信息
     *
     * @param sequence 画集序列号
     * @return 画集信息
     */
    CollectionInfoResponse getCollectionInfo(long sequence);

    /**
     * 随机打开一个画集
     */
    CollectionInfoResponse getRandomCollection();

    /**
     * 随机打开一个画集
     */
    CollectionInfoResponse getRandomCollectionByType(String type);

    /**
     * 获取画集预览
     */
    File getCollectionThumbnail(long sequence);

    /**
     * 下载整个画集
     *
     * @param sequence 画集序列号
     * @return 打包好的文件
     */
    File download(long sequence) throws IllegalFileRequestException;

    /**
     * 修改画集名
     */
    ModifyResultResponse modifyName(long sequence, String newName);

    /**
     * 删除一个画集
     *
     * @param sequence 画集序列号
     * @return 处理结果
     */
    StatusCode delete(long sequence);
}
