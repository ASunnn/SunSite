package sunnn.sunsite.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import sunnn.sunsite.util.StatusCode;

import java.sql.Timestamp;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class CollectionInfoResponse extends BaseResponse {

    private String sequence;

    private String collection;

    private String group;

    private String type;

    int post;

    String lastUpdate;

    public CollectionInfoResponse(StatusCode statusCode) {
        super(statusCode);
    }

    public CollectionInfoResponse setSequence(long sequence) {
        this.sequence = String.valueOf(sequence);
        return this;
    }

    public CollectionInfoResponse setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate.toString();
        return this;
    }
}
