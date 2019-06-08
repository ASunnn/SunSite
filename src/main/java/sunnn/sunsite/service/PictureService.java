package sunnn.sunsite.service;

import org.springframework.web.multipart.MultipartFile;
import sunnn.sunsite.dto.request.UploadPictureInfo;
import sunnn.sunsite.dto.response.ModifyResultResponse;
import sunnn.sunsite.util.StatusCode;

public interface PictureService {

    /**
     * 对上传的文件进行保存
     *
     * @param file 图片文件
     * @return 检查结果
     */
    StatusCode uploadPicture(MultipartFile file, String uploadCode);

    /**
     * 保存上传的图片记录
     *
     * @param info 图片信息
     * @return 保存结果
     */
    StatusCode uploadInfoAndSave(UploadPictureInfo info);

    /**
     * 修改图片信息（名字）
     */
    ModifyResultResponse modifyPicture(long sequence, String newName);

    /**
     * 修改图片绘师
     */
    StatusCode modifyIllustrator(long sequence, String illustrators);

    /**
     * 删除一个图片记录
     *
     * @param sequence 图片序列号
     * @return 处理结果
     */
    StatusCode delete(long sequence);

    /**
     * 删除整个画集的图片
     *
     * @param sequence 画集序列号
     * @return 处理结果
     */
    StatusCode deleteCollection(long sequence);
}
