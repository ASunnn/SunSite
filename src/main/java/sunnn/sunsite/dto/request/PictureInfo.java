package sunnn.sunsite.dto.request;

import javax.validation.constraints.NotBlank;

public class PictureInfo {

    @NotBlank(message = "Illustrator Can't Be Empty")
    private String illustrator;

    @NotBlank(message = "Collection Can't Be Empty")
    private String collection;

    @NotBlank(message = "Type Can't Be Empty")
    private String type;

    private String uploadCode;

    public PictureInfo() {
    }   //必须要有空construct，不然json转换时会报错

    public PictureInfo(String illustrator, String collection, String uploadCode, String type) {
        this.illustrator = illustrator;
        this.collection = collection;
        this.type = type;
    }

    public String getIllustrator() {
        return illustrator;
    }

    public void setIllustrator(String illustrator) {
        this.illustrator = illustrator;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUploadCode() {
        return uploadCode;
    }

    public void setUploadCode(String uploadCode) {
        this.uploadCode = uploadCode;
    }

    @Override
    public String toString() {
        return "PictureInfo{" +
                "illustrator='" + illustrator + '\'' +
                ", collection='" + collection + '\'' +
                ", type='" + type + '\'' +
                ", uploadCode='" + uploadCode + '\'' +
                '}';
    }
}
