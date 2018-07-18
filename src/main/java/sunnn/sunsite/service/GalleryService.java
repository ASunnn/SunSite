package sunnn.sunsite.service;

import org.springframework.web.multipart.MultipartFile;
import sunnn.sunsite.dto.response.PictureListResponse;
import sunnn.sunsite.util.StatusCode;
import sunnn.sunsite.dto.request.PictureInfo;

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
    boolean saveUpload(PictureInfo info);

}
