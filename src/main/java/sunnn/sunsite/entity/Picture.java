package sunnn.sunsite.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 图册实体类
 */
@Document(collection = "gallery")
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class Picture {
    @Id
    private String id;

    /**
     * 文件名
     */
    @Field(value = "name")
    private String fileName;

    /**
     * 存储路径
     */
    @Field(value = "path")
    private String path;

    /**
     * 缩略图文件名
     */
    @Field(value = "thumbnail")
    private String thumbnailName;

    /**
     * 画师
     */
    @Field(value = "illustrator")
    private Illustrator illustrator;

    /**
     * 上传（到本系统的）时间
     */
    @Field(value = "uploadTime")
    private long uploadTime;

    /**
     * 插画所属
     * 散图/本子/画集
     */
    @DBRef
    private Collection collection;

    public static final String THUMBNAIL_PREFIX = "m_";
}
