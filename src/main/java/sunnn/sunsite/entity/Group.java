package sunnn.sunsite.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * 社团
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class Group {

    private int id;

    /**
     * 社团名
     */
    private String name;

    /**
     * 最后更新时间
     */
    private Timestamp lastUpdate;

    public Group() {
    }

    public Group(int id, String name, Timestamp lastUpdate) {
        this.id = id;
        this.name = name;
        this.lastUpdate = lastUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return Objects.equals(name, group.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }
}
