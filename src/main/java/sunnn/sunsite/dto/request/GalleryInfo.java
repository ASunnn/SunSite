package sunnn.sunsite.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Deprecated
public class GalleryInfo {

    @NotNull
    private String illustrator;

    @NotNull
    private String collection;

    @NotNull
    private String type;

}
