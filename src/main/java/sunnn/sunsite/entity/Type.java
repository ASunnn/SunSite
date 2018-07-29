package sunnn.sunsite.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 图的类型
 */
@Document(collection = "type")
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class Type {

    @Id
    private String id;

    /**
     * 类型名
     */
    @Field(value = "name")
    private String name;

    public Type(String name) {
        this.name = name;
    }
}
