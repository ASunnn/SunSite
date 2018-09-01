package sunnn.sunsite.dto;

/**
 * 将基本数据类型真·装箱
 *
 * @param <T> 数据类型
 * @author ASun
 */
public class BaseDataBoxing<T extends Number> {

    public T number;

    public BaseDataBoxing(T number) {
        this.number = number;
    }
}
