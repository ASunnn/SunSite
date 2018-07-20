package sunnn.sunsite.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 画师
 */
@Document(collection = "illustrator")
@Getter
@Setter
@ToString
public class Illustrator {

    @Id
    private String id;

    /**
     * 画师名
     */
    @Field(value = "name")
    private String name;

    public Illustrator(String name) {
        this.name = name;
    }
}
