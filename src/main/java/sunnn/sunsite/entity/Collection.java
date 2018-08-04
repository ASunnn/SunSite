package sunnn.sunsite.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Objects;

/**
 * 图片所属的文件夹
 */
@Document(collection = "collection")
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class Collection {

    @Id
    private String id;

    /**
     * 文件夹名
     */
    @Field(value = "name")
    private String name;

    /**
     * 图片类型
     */
    @Field(value = "typex")
    private Type type;

    public Collection(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Collection that = (Collection) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, type);
    }
}
