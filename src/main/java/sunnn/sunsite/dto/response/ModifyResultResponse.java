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

    private String newLink;

    public ModifyResultResponse(StatusCode statusCode) {
        super(statusCode);
    }

    public ModifyResultResponse(StatusCode statusCode, String newLink) {
        super(statusCode);
        this.newLink = newLink;
    }

    public ModifyResultResponse(StatusCode statusCode, long newLink) {
        super(statusCode);
        this.newLink = String.valueOf(newLink);
    }
}
