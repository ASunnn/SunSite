package sunnn.sunsite.service;

import sunnn.sunsite.dto.response.TypeListResponse;
import sunnn.sunsite.entity.Type;
import sunnn.sunsite.exception.IllegalFileRequestException;
import sunnn.sunsite.util.StatusCode;

import java.io.File;

public interface TypeService {

    /**
     * 创建新的类型
     *
     * @param name 新类型
     * @return 如果记录已经存在，直接返回记录，否则创建后再返回
     */
    Type createGroup(String name);

    /**
     * 获取类型列表
     *
     * @return 类型列表
     */
    TypeListResponse getTypeList();

    /**
     * 下载整个类型的图片
     *
     * @param name 类型名
     * @return 打包好的文件
     */
    File download(String name) throws IllegalFileRequestException;

    /**
     * 删除一种类型
     *
     * @param name 类型名
     * @return 处理结果
     */
    StatusCode delete(String name);
}
