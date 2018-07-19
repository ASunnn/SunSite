package sunnn.sunsite.dto.response;

import sunnn.sunsite.util.StatusCode;

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

    public String[] getFileList() {
        return fileList;
    }

    public void setFileList(String[] fileList) {
        this.fileList = fileList;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }
}
