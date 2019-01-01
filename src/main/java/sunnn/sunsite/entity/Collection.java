package sunnn.sunsite.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * 画集
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class Collection {

    /**
     * 画集序列号
     */
    private long cId;

    /**
     * 画集名
     */
    private String name;

    /**
     * 所属社团
     */
    private int group;

    /**
     * 画集类型
     */
    private int type;

    /**
     * 创建时间
     */
    private Timestamp createTime;

    /**
     * 最后更新时间
     */
    private Timestamp lastUpdate;

    public Collection() {
    }

    public Collection(long cId, String name, int group, int type, Timestamp createTime, Timestamp lastUpdate) {
        this.cId = cId;
        this.name = name;
        this.group = group;
        this.type = type;
        this.createTime = createTime;
        this.lastUpdate = lastUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Collection that = (Collection) o;
        return cId == that.cId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(cId);
    }
}
