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
public class PictureInfoInfoListResponse extends BaseResponse {

    private String[] infoList;

    private String[] thumbnailSequenceList;

    public PictureInfoInfoListResponse(StatusCode statusCode) {
        super(statusCode);
    }

    public PictureInfoInfoListResponse(
            StatusCode statusCode, String[] infoList, String[] thumbnailSequenceList) {
        super(statusCode);
        this.infoList = infoList;
        this.thumbnailSequenceList = thumbnailSequenceList;
    }
}
