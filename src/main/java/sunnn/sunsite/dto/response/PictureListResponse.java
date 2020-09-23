package sunnn.sunsite.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import sunnn.sunsite.dto.Convertible;
import sunnn.sunsite.dto.PicInfo;
import sunnn.sunsite.util.StatusCode;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class PictureListResponse extends BaseResponse implements Convertible<PictureListResponse, PicInfo> {

    private PictureListInfo[] pictureList;

    private int pageCount;

    public PictureListResponse(StatusCode statusCode) {
        super(statusCode);
    }

    @Override
    public PictureListResponse convertTo(List<PicInfo> entity) {
        if (entity.isEmpty())
            return this;

        PictureListInfo[] pictureList = new PictureListInfo[entity.size()];
        for (int i = 0; i < entity.size(); ++i) {
            PictureListInfo info = new PictureListInfo();
            info.sequence = String.valueOf(entity.get(i).getSequence());
            info.name = entity.get(i).getName();
            info.group = entity.get(i).getGroup();
            info.cId = String.valueOf(entity.get(i).getCId());
            info.collection = entity.get(i).getCollection();

            pictureList[i] = info;
        }

        return setPictureList(pictureList);
    }
}

@Getter
@Setter
class PictureListInfo {

    String sequence;

    String name;

    String group;

    String cId;

    String collection;
}