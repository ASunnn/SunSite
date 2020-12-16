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
public class GroupInfoResponse extends BaseResponse {

    private String group;

    private String[] alias;

    int book;

    int post;

    String lastUpdate;

    public GroupInfoResponse(StatusCode statusCode) {
        super(statusCode);
    }

    public GroupInfoResponse setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate.toString();
        return this;
    }
}
