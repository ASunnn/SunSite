package sunnn.sunsite.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class PictureListWithFilter {

    @NotNull
    private String type;

    @NotNull
    private String collection;

    @NotNull
    private String illustrator;

    @NotNull
    private String name;

    private int page;

    private int size;
}
