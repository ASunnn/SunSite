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
public class CollectionListResponse extends BaseResponse {

    private String[] collectionList;

    private String[] thumbnailSequenceList;

    public CollectionListResponse(StatusCode statusCode) {
        super(statusCode);
    }

    public CollectionListResponse(
            StatusCode statusCode, String[] collectionList, String[] thumbnailSequenceList) {
        super(statusCode);
        this.collectionList = collectionList;
        this.thumbnailSequenceList = thumbnailSequenceList;
    }
}
