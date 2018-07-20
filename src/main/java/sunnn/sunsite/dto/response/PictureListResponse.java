package sunnn.sunsite.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sunnn.sunsite.util.StatusCode;

@Getter
@Setter
@ToString
public class PictureListResponse extends BaseResponse {

    private String[] fileList;

    private int pageCount;

    public PictureListResponse(StatusCode statusCode) {
        super(statusCode);
    }

    public PictureListResponse(StatusCode statusCode, String[] pictureList, int pageCount) {
        super(statusCode);
        this.fileList = pictureList;
        this.pageCount = pageCount;
    }
}
