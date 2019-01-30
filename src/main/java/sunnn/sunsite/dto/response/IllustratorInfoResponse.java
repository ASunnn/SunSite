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
public class IllustratorInfoResponse extends BaseResponse {

    private String illustrator;

    private String alias;

    public IllustratorInfoResponse(StatusCode statusCode) {
        super(statusCode);
    }
}
