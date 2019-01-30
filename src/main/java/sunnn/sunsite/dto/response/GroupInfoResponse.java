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
public class GroupInfoResponse extends BaseResponse {

    private String group;

    private String alias;

    public GroupInfoResponse(StatusCode statusCode) {
        super(statusCode);
    }
}
