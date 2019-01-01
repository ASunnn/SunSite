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

    private String sequence;

    private String name;

    private String group;

    private String cId;

    private String collection;

    private String[] illustrator;

    private String[] tag;

    private int width;

    private int height;

    private String prev;

    private String next;

    public PictureInfoResponse(StatusCode statusCode) {
        super(statusCode);
    }

    public PictureInfoResponse setSequence(long sequence) {
        this.sequence = String.valueOf(sequence);
        return this;
    }

    public PictureInfoResponse setPrev(long prev) {
        this.prev = String.valueOf(prev);
        return this;
    }

    public PictureInfoResponse setNext(long next) {
        this.next = String.valueOf(next);
        return this;
    }
}


