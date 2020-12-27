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
public class TypeInfoResponse extends BaseResponse {

    private String type;

    private int book;

    private String lastUpdate;

    public TypeInfoResponse(StatusCode statusCode) {
        super(statusCode);
    }
}
