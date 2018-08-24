package sunnn.sunsite.service;

import org.springframework.web.multipart.MultipartFile;
import sunnn.sunsite.dto.request.PictureListWithFilter;
import sunnn.sunsite.dto.response.PictureInfoResponse;
import sunnn.sunsite.dto.response.PictureListResponse;
import sunnn.sunsite.exception.IllegalFileRequestException;
import sunnn.sunsite.util.StatusCode;
import sunnn.sunsite.dto.request.UploadPictureInfo;

import java.io.File;
import java.util.List;

public interface GalleryService {

    /**
     * 根据请求获取图片文件列表
     *
     * @param page     页码
     * @param pageSize 每页数量
     * @return 图片文件列表
     */
    PictureListResponse getPictureList(int page, int pageSize);

    /**
     * 根据过滤请求获取图片文件列表
     *
     * @param filter 过滤请求
     * @return 查询结果
     */
    PictureListResponse getPictureListWithFilter(PictureListWithFilter filter);

    /**
     * 获取图片缩略图
     *
     * @param sequenceCode 图片序列号
     * @return 缩略图文件
     */
    File getThumbnail(long sequenceCode);

    /**
     * 获取图片本体
     *
     * @param illustrator 绘师
     * @param collection  画集
     * @param fileName    图片名
     * @return 图片文件
     * @throws IllegalFileRequestException 请求了非法的文件
     */
    File getPictureFile(String illustrator, String collection, String fileName) throws IllegalFileRequestException;


    /**
     * 获取图片信息
     *
     * @param sequenceCode 图片序列号
     * @param extra        是否查询图片的前后信息
     * @return 图片信息
     */
    PictureInfoResponse getPictureInfo(long sequenceCode, boolean extra);

    /**
     * 根据过滤条件获取图库信息（类型、画师、画集
     *
     * @param type        类型
     * @param illustrator 画师
     * @param collection  画集
     */
    void getGalleryInfo(List<String> illustrator, List<String> collection, List<String> type);

    /**
     * 检查文件合法性
     *
     * @param file 图片文件
     * @return 检查结果
     */
    StatusCode checkFile(MultipartFile file, String uploadCode);

    /**
     * 保存上传的内容
     *
     * @return 保存结果
     */
    StatusCode saveUpload(UploadPictureInfo info);

    /**
     * 删除图片
     *
     * @param sequenceCode 图片序列号
     */
    StatusCode deletePicture(long sequenceCode);

}
