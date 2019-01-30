package sunnn.sunsite.service;

import sunnn.sunsite.dto.response.PictureInfoResponse;
import sunnn.sunsite.dto.response.PictureListResponse;
import sunnn.sunsite.exception.IllegalFileRequestException;

import java.io.File;

public interface GalleryService {

    /**
     * 根据请求获取图片列表
     *
     * @param page 页码
     * @return 图片列表
     */
    PictureListResponse getPictureList(int page);

    /**
     * 获取画集里的图片列表
     *
     * @param page 页码
     * @return 图片列表
     */
    PictureListResponse getPictureListInCollection(long cId, int page);

    /**
     * 获取一个画师的图片
     *
     * @param illustrator 画师
     * @param page        页码
     * @return 图片列表
     */
    PictureListResponse getPictureListByiIllustrator(String illustrator, int page);

    /**
     * 获取图片缩略图
     *
     * @param sequence 图片序列号
     * @return 缩略图文件
     */
    File getThumbnail(long sequence);

    /**
     * 获取图片本体
     *
     * @param sequence 图片序列号
     * @return 图片文件
     * @throws IllegalFileRequestException 请求了非法的文件
     */
    File getPictureFile(long sequence) throws IllegalFileRequestException;


    /**
     * 获取图片信息
     *
     * @param sequence 图片序列号
     * @return 图片信息
     */
    PictureInfoResponse getPictureInfo(long sequence);
}
