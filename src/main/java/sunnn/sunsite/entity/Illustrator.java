package sunnn.sunsite.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Objects;

/**
 * 画师
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class Illustrator {

    private int id;

    /**
     * 画师名
     */
    private String name;

    public Illustrator() {
    }

    public Illustrator(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static final String DEFAULT_VALUE = "None";

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Illustrator that = (Illustrator) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }
}
