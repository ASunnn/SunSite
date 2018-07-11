package sunnn.sunsite.message.request;

public class PicInfo {

    private String illustrator;

    private String collection;

    private String adultOnly;

    private String type;

    public PicInfo(String illustrator, String collection, String adultOnly, String type) {
        this.illustrator = illustrator;
        this.collection = collection;
        this.adultOnly = adultOnly;
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

    public String getAdultOnly() {
        return adultOnly;
    }

    public void setAdultOnly(String adultOnly) {
        this.adultOnly = adultOnly;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "PicInfo{" +
                "illustrator='" + illustrator + '\'' +
                ", collection='" + collection + '\'' +
                ", adultOnly='" + adultOnly + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
