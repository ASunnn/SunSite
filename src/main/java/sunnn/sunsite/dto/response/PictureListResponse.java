package sunnn.sunsite.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import sunnn.sunsite.entity.Picture;
import sunnn.sunsite.util.Convertible;
import sunnn.sunsite.util.StatusCode;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class PictureListResponse extends BaseResponse implements Convertible<PictureListResponse, Picture> {

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

    @Override
    public PictureListResponse convertTo(List<Picture> pictures) {
        if (pictures.isEmpty())
            return this;

        String[] fileNameList = new String[pictures.size()];
        for (int i = 0; i < pictures.size(); ++i)
            fileNameList[i] =
                    String.valueOf(pictures.get(i).getSequence());

        return setFileList(fileNameList);
    }
}
