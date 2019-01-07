package sunnn.sunsite.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class UploadPictureInfo {

    private String illustrator;

    @NotBlank
    private String group;

    @NotBlank
    private String collection;

    @NotBlank
    private String uploadCode;
}
