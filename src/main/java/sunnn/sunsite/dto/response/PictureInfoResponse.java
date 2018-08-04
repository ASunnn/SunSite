package sunnn.sunsite.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import sunnn.sunsite.util.StatusCode;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class PictureInfoResponse extends BaseResponse {

    private String name;

    private String illustrator;

    private String collection;

    private String prev;

    private String next;

    public PictureInfoResponse(StatusCode statusCode) {
        super(statusCode);
    }

    public PictureInfoResponse(StatusCode statusCode, String name, String illustrator, String collection) {
        super(statusCode);
        this.name = name;
        this.illustrator = illustrator;
        this.collection = collection;
    }

    public PictureInfoResponse(StatusCode statusCode, String name, String illustrator, String collection, String prev, String next) {
        super(statusCode);
        this.name = name;
        this.illustrator = illustrator;
        this.collection = collection;
        this.prev = prev;
        this.next = next;
    }
}


