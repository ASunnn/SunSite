package sunnn.sunsite.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import sunnn.sunsite.dto.CollectionInfo;
import sunnn.sunsite.dto.Convertible;
import sunnn.sunsite.util.StatusCode;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class CollectionListResponse extends BaseResponse implements Convertible<CollectionListResponse, CollectionInfo> {

    private CollectionListInfo[] collectionList;

    private int pageCount;

    public CollectionListResponse(StatusCode statusCode) {
        super(statusCode);
    }

    @Override
    public CollectionListResponse convertTo(List<CollectionInfo> entity) {
        if (entity.isEmpty())
            return this;

        CollectionListInfo[] collectionList = new CollectionListInfo[entity.size()];
        for (int i = 0; i < entity.size(); ++i) {
            CollectionListInfo info = new CollectionListInfo();
            info.sequence = String.valueOf(entity.get(i).getSequence());
            info.group = entity.get(i).getGroup();
            info.collection = entity.get(i).getCollection();
            info.type = entity.get(i).getType();
            info.post = entity.get(i).getPost();
            info.lastUpdate = entity.get(i).getLastUpdate().toString();
            collectionList[i] = info;
        }

        return setCollectionList(collectionList);
    }
}

@Getter
@Setter
class CollectionListInfo {

    String sequence;

    String group;

    String collection;

    String type;

    int post;

    String lastUpdate;
}
