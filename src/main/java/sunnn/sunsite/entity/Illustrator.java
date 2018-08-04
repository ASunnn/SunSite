package sunnn.sunsite.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import sunnn.sunsite.util.SunSiteConstant;

import java.io.File;
import java.util.Objects;

/**
 * 画师
 */
@Document(collection = "illustrator")
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class Illustrator {

    @Id
    private String id;

    /**
     * 画师名
     */
    @Field(value = "name")
    private String name;

    /**
     * 该画师图片的保存路径
     */
    @Field(value = "path")
    private String path;

    public Illustrator(String name) {
        this.name = name;
        this.path = SunSiteConstant.picturePath
                + name
                + SunSiteConstant.pathSeparator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Illustrator that = (Illustrator) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, path);
    }
}
