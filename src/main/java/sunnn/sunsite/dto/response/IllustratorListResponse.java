package sunnn.sunsite.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import sunnn.sunsite.dto.Convertible;
import sunnn.sunsite.dto.IllustratorInfo;
import sunnn.sunsite.util.StatusCode;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class IllustratorListResponse extends BaseResponse {

    private IllustratorInfo[] list;

    private int pageCount;

    public IllustratorListResponse(StatusCode statusCode) {
        super(statusCode);
    }
}
