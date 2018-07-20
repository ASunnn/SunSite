package sunnn.sunsite.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sunnn.sunsite.util.StatusCode;

@Getter
@Setter
@ToString
public class BaseResponse {

    private int code;

    private String msg;

    public BaseResponse(StatusCode statusCode) {
        this.code = statusCode.getCode();
        this.msg = statusCode.name();
    }
}
