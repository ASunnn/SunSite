package sunnn.sunsite.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * 图片实体类
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class Pic {

    private String id;

    /**
     * 插画的序列号
     */
    private long sequence;

    /**
     * 文件名
     */
    private String name;

    /**
     * 文件大小
     */
    private long size;

    /**
     * 图片宽度
     */
    private int width;

    /**
     * 图片高度
     */
    private int height;

    /**
     * 图片是竖的还是横的
     * 1：横
     * 0：方
     * -1：竖
     */
    private int vOrH;

    /**
     * 上传（到本系统的）时间
     */
    private Timestamp uploadTime;

    /**
     * 缩略图文件名
     */
    private String thumbnailName;

    /**
     * 存储路径
     */
    private String path;

    public static final String THUMBNAIL_PREFIX = "thumbnail_";
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pic picture = (Pic) o;
        return sequence == picture.sequence;
    }

    @Override
    public int hashCode() {

        return Objects.hash(sequence);
    }
}
