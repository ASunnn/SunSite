package sunnn.sunsite.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Objects;

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
     * 图片高度
     */
    @Field(value = "height")
    private int height;

    /**
     * 图片宽度
     */
    @Field(value = "width")
    private int width;

    /**
     * 图片是竖的还是横的
     * 1：竖
     * 0：方
     * -1：横
     */
    @Field(value = "vOrH")
    private int vOrH;

    /**
     * 文件大小
     */
    @Field(value = "size")
    private long size;

    /**
     * 上传（到本系统的）时间
     */
    @Field(value = "uploadTime")
    private long uploadTime;

    /**
     * 画师
     */
    @Field(value = "illustrator")
    private Illustrator illustrator;

    /**
     * 插画所属
     * 散图/本子/画集
     */
    @Field(value = "collection")
    private Collection collection;

    /**
     * 插画类型
     */
    @Field(value = "type")
    private Type type;

    /**
     * 插画的序列号
     */
    @Field(value = "sequence")
    private long sequence;

    public static final String THUMBNAIL_PREFIX = "thumbnail_";

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
