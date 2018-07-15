package sunnn.sunsite.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 图片所属的文件夹
 */
@Document(collection = "collection")
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
    @DBRef
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
}
