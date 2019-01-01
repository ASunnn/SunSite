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
public class CollectionInfoResponse extends BaseResponse implements Convertible<CollectionInfoResponse, PictureBase> {

    private ListInfo[] pictureList;

    private int pageCount;

    private String collection;

    private String group;

    public CollectionInfoResponse(StatusCode statusCode) {
        super(statusCode);
    }

    @Override
    public CollectionInfoResponse convertTo(List<PictureBase> entity) {
        if (entity.isEmpty())
            return this;

        ListInfo[] pictureList = new ListInfo[entity.size()];
        for (int i = 0; i < entity.size(); ++i) {
            ListInfo info = new ListInfo();
            info.sequence = String.valueOf(entity.get(i).getSequence());
            info.name = entity.get(i).getName();

            pictureList[i] = info;
        }

        return setPictureList(pictureList);
    }
}

@Getter
@Setter
class ListInfo {

    String sequence;

    String name;
}
