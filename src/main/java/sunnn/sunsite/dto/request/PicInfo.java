package sunnn.sunsite.dto.request;

public class PicInfo {

    private String illustrator;

    private String collection;

    private String type;

    private String uploadCode;

    public PicInfo() {
    }   //必须要有空construct，不然json转换时会报错

    public PicInfo(String illustrator, String collection, String uploadCode, String type) {
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
        return "PicInfo{" +
                "illustrator='" + illustrator + '\'' +
                ", collection='" + collection + '\'' +
                ", type='" + type + '\'' +
                ", uploadCode='" + uploadCode + '\'' +
                '}';
    }
}
