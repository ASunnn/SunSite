package sunnn.sunsite.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Objects;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class Picture {

    private long sequence;

    private String name;

    private long collection;

    private int index;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Picture picture = (Picture) o;
        return sequence == picture.sequence;
    }

    @Override
    public int hashCode() {

        return Objects.hash(sequence);
    }
}
