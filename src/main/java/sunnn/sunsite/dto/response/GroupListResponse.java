package sunnn.sunsite.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import sunnn.sunsite.dto.Convertible;
import sunnn.sunsite.dto.GroupInfo;
import sunnn.sunsite.util.StatusCode;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class GroupListResponse extends BaseResponse {

    private GroupInfo[] groupList;

    private int pageCount;

    public GroupListResponse(StatusCode statusCode) {
        super(statusCode);
    }
}