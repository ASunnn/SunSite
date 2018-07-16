package sunnn.sunsite.dto.response;

import sunnn.sunsite.util.StatusCode;

public class FileUploadResponse extends BaseResponse {

    private String[] duplicatedFile;

    private String uploadCode;

    public FileUploadResponse(StatusCode statusCode) {
        super(statusCode);
    }

    public FileUploadResponse(StatusCode statusCode, String uploadCode) {
        super(statusCode);
        this.uploadCode = uploadCode;
    }

    public FileUploadResponse(StatusCode statusCode, String[] duplicatedFile) {
        super(statusCode);
        this.duplicatedFile = duplicatedFile;
    }

    public String[] getDuplicatedFile() {
        return duplicatedFile;
    }

    public void setDuplicatedFile(String[] duplicatedFile) {
        this.duplicatedFile = duplicatedFile;
    }

    public String getUploadCode() {
        return uploadCode;
    }

    public void setUploadCode(String uploadCode) {
        this.uploadCode = uploadCode;
    }
}
