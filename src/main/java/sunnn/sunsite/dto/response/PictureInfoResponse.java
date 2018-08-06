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

    private String illustrator;

    private String collection;

    private String prev;

    private String next;

    public PictureInfoResponse(StatusCode statusCode) {
        super(statusCode);
    }

    public PictureInfoResponse(StatusCode statusCode, long sequence, String name, String illustrator, String collection) {
        super(statusCode);
        this.sequence = String.valueOf(sequence);
        this.name = name;
        this.illustrator = illustrator;
        this.collection = collection;
    }

    public PictureInfoResponse(StatusCode statusCode, long sequence, String name, String illustrator, String collection, long prev, long next) {
        super(statusCode);
        this.sequence = String.valueOf(sequence);
        this.name = name;
        this.illustrator = illustrator;
        this.collection = collection;
        this.prev = String.valueOf(prev);
        this.next = String.valueOf(next);
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


