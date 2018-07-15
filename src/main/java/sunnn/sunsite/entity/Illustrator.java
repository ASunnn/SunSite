package sunnn.sunsite.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 画师
 */
@Document(collection = "illustrator")
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
}
