package sunnn.sunsite.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 图册实体类
 */
@Document(collection = "gallery")
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Illustrator getIllustrator() {
        return illustrator;
    }

    public void setIllustrator(Illustrator illustrator) {
        this.illustrator = illustrator;
    }

    public long getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(long uploadTime) {
        this.uploadTime = uploadTime;
    }

    public Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    @Override
    public String toString() {
        return "Picture{" +
                "id='" + id + '\'' +
                ", fileName='" + fileName + '\'' +
                ", path='" + path + '\'' +
                ", illustrator=" + illustrator +
                ", uploadTime=" + uploadTime +
                ", collection=" + collection +
                '}';
    }
}
