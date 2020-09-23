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
public class CollectionInfoResponse extends BaseResponse {

    private String sequence;

    private String collection;

    private String group;

    private String type;

    public CollectionInfoResponse(StatusCode statusCode) {
        super(statusCode);
    }

    public CollectionInfoResponse setSequence(long sequence) {
        this.sequence = String.valueOf(sequence);
        return this;
    }
}
