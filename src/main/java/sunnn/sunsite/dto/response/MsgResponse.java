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
public class MsgResponse extends BaseResponse {

    private String msg;

    public MsgResponse(StatusCode statusCode) {
        super(statusCode);
    }
}
