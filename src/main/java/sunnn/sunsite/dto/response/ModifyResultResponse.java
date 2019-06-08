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
public class ModifyResultResponse extends BaseResponse {

    private String newLinkInfo;

    public ModifyResultResponse(StatusCode statusCode) {
        super(statusCode);
    }

    public ModifyResultResponse(StatusCode statusCode, String newLinkInfo) {
        super(statusCode);
        this.newLinkInfo = newLinkInfo;
    }

    public ModifyResultResponse(StatusCode statusCode, long newLinkInfo) {
        super(statusCode);
        this.newLinkInfo = String.valueOf(newLinkInfo);
    }
}
