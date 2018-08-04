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

    private int[] fileList;

    private int pageCount;

    public PictureListResponse(StatusCode statusCode) {
        super(statusCode);
    }

    public PictureListResponse(StatusCode statusCode, int[] pictureList, int pageCount) {
        super(statusCode);
        this.fileList = pictureList;
        this.pageCount = pageCount;
    }


    @Override
    public PictureListResponse convertTo(List<Picture> pictures) {
        if (pictures.isEmpty())
            return this;

        int[] fileNameList = new int[pictures.size()];
        for (int i = 0; i < pictures.size(); ++i)
            fileNameList[i] =
                    pictures.get(i).getSequence();
        return setFileList(fileNameList);
    }
}
