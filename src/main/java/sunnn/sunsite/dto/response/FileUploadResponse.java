package sunnn.sunsite.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sunnn.sunsite.util.StatusCode;

@Getter
@Setter
@ToString
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
}
