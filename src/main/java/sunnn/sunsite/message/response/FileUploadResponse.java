package sunnn.sunsite.message.response;

import sunnn.sunsite.message.StatusCode;

public class FileUploadResponse extends BaseResponse {

    private String[] duplicatedFile;

    public FileUploadResponse(StatusCode statusCode) {
        super(statusCode);
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
}
