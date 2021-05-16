package sunnn.sunsite.recover;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class RecoverCollection {

    private String type;

    private String group;

    private String collection;

    private long cId;

    private int count;
}
