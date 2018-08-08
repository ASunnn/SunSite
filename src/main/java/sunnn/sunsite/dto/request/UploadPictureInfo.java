package sunnn.sunsite.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
public class UploadPictureInfo {

    @NotBlank
    private String illustrator;

    @NotBlank
    private String collection;

    @NotBlank
    private String type;

    @NotBlank
    private String uploadCode;
}
