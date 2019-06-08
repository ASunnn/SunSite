package sunnn.sunsite.service;

public interface MessageBoxService {

    /**
     * 从信息箱中获取一条消息
     *
     * @return 正常情况下返回一个字符串，若已无信息返回null
     */
    String getMessage();

    /**
     * 放置一条信息到箱中
     */
    void pushMessage(String msg);
}
