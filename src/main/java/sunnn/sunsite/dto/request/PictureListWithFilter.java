package sunnn.sunsite.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PictureListWithFilter {

    private String type;

    private String collection;

    private String illustrator;

    private String name;

    private int page;

    private int size;
}
