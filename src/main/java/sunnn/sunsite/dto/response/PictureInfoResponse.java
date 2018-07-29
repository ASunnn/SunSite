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
public class PictureInfoResponse extends BaseResponse {

    private String illustrator;

    private String collection;

    public PictureInfoResponse(StatusCode statusCode) {
        super(statusCode);
    }

    public PictureInfoResponse(StatusCode statusCode, String illustrator, String collection) {
        super(statusCode);
        this.illustrator = illustrator;
        this.collection = collection;
    }
}
