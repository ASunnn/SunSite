package sunnn.sunsite.service;

import sunnn.sunsite.dto.response.IllustratorListResponse;
import sunnn.sunsite.entity.Illustrator;
import sunnn.sunsite.exception.IllegalFileRequestException;
import sunnn.sunsite.util.StatusCode;

import java.io.File;

public interface IllustratorService {

    /**
     * 创建新的画师记录
     *
     * @param name 新画师
     * @return 如果记录已经存在，直接返回记录，否则创建后再返回
     */
    Illustrator createIllustrator(String name);

    /**
     * 获取画师列表
     *
     * @param page 页码
     * @return 画师列表
     */
    IllustratorListResponse getIllustratorList(int page);

    /**
     * 下载画师的所有图片
     *
     * @param name 画师名
     * @return 打包好的文件
     */
    File download(String name) throws IllegalFileRequestException;

    /**
     * 删除一个画师记录
     *
     * @param name 图片序列号
     * @return 处理结果
     */
    StatusCode delete(String name);
}
