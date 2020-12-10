package sunnn.sunsite.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class UploadPictureInfo {

    private String[] illustrator;

    private String group;

    private String collection;
}
