package sunnn.sunsite.dto.response;

import sunnn.sunsite.util.StatusCode;

public class PictureListResponse extends BaseResponse {

    private String[] pictureList;

    private int pageCount;

    public PictureListResponse(StatusCode statusCode) {
        super(statusCode);
    }

    public PictureListResponse(StatusCode statusCode, String[] pictureList, int pageCount) {
        super(statusCode);
        this.pictureList = pictureList;
        this.pageCount = pageCount;
    }

    public String[] getPictureList() {
        return pictureList;
    }

    public void setPictureList(String[] pictureList) {
        this.pictureList = pictureList;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }
}
