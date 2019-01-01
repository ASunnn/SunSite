package sunnn.sunsite.service;

import sunnn.sunsite.dto.response.GroupListResponse;
import sunnn.sunsite.entity.Group;
import sunnn.sunsite.exception.IllegalFileRequestException;
import sunnn.sunsite.util.StatusCode;

import java.io.File;

public interface GroupService {

    /**
     * 创建新的社团
     *
     * @param name 新社团信息
     * @return 如果记录已经存在，直接返回记录，否则创建后再返回
     */
    Group createGroup(String name);

    /**
     * 获取社团列表
     *
     * @param page 页码
     * @return 社团列表
     */
    GroupListResponse getGroupList(int page);

    /**
     * 下载整个社团图片
     *
     * @param name 社团名
     * @return 打包好的文件
     */
    File download(String name) throws IllegalFileRequestException;

    /**
     * 删除一个社团记录
     *
     * @param name 社团名
     * @return 处理结果
     */
    StatusCode delete(String name);
}
