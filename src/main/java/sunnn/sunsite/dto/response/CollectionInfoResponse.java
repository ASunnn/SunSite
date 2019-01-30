package sunnn.sunsite.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import sunnn.sunsite.dto.Convertible;
import sunnn.sunsite.dto.PictureBase;
import sunnn.sunsite.util.StatusCode;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class CollectionInfoResponse extends BaseResponse {

    private String collection;

    private String group;

    private String type;

    public CollectionInfoResponse(StatusCode statusCode) {
        super(statusCode);
    }
}
