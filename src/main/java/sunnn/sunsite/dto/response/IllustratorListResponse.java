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
public class IllustratorListResponse extends BaseResponse implements Convertible<IllustratorListResponse, IllustratorInfo> {

    private IllustratorInfo[] illustratorList;

    private int pageCount;

    public IllustratorListResponse(StatusCode statusCode) {
        super(statusCode);
    }

    @Override
    public IllustratorListResponse convertTo(List<IllustratorInfo> entity) {
        if (entity.isEmpty())
            return this;

        IllustratorInfo[] illustratorList = new IllustratorInfo[entity.size()];
        entity.toArray(illustratorList);

        return setIllustratorList(illustratorList);
    }
}
