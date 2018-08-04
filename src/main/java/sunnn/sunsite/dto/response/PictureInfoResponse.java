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

    private int sequence;

    private String name;

    private String illustrator;

    private String collection;

    private int prev;

    private int next;

    public PictureInfoResponse(StatusCode statusCode) {
        super(statusCode);
    }

    public PictureInfoResponse(StatusCode statusCode, int sequence, String name, String illustrator, String collection) {
        super(statusCode);
        this.sequence = sequence;
        this.name = name;
        this.illustrator = illustrator;
        this.collection = collection;
    }

    public PictureInfoResponse(StatusCode statusCode, int sequence, String name, String illustrator, String collection, int prev, int next) {
        super(statusCode);
        this.sequence = sequence;
        this.name = name;
        this.illustrator = illustrator;
        this.collection = collection;
        this.prev = prev;
        this.next = next;
    }
}


