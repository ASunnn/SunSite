package sunnn.sunsite.service;

import org.springframework.web.multipart.MultipartFile;
import sunnn.sunsite.dto.response.PictureListResponse;
import sunnn.sunsite.entity.Picture;
import sunnn.sunsite.util.StatusCode;
import sunnn.sunsite.dto.request.PictureInfo;

import java.io.File;

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
     * 获取图片缩略图
     *
     * @param pictureName 图片名
     * @return 缩略图文件
     */
    File getThumbnail(String pictureName);

    /**
     * 获取图片信息
     *
     * @param pictureName 图片名
     * @return 图片信息
     */
    Picture getPictureInfo(String pictureName);

    /**
     * 获取图片本体
     *
     * @param pictureName 图片名
     * @return 图片文件
     */
    File getPictureFile(String pictureName);

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

    /**
     * 删除图片
     *
     * @param pictureName 图片名
     */
    StatusCode deletePicture(String pictureName);

}
