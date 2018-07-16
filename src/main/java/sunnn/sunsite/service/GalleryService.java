package sunnn.sunsite.service;

import org.springframework.web.multipart.MultipartFile;
import sunnn.sunsite.util.StatusCode;
import sunnn.sunsite.dto.request.PictureInfo;

public interface GalleryService {

    /**
     * 检查文件合法性
     * @param file  图片文件
     * @return 检查结果
     */
    StatusCode checkFile(MultipartFile file, String uploadCode);

    /**
     * 保存上传的内容
     * @return 保存结果
     */
    boolean saveUpload(PictureInfo info);

}
