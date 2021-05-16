package sunnn.sunsite.recover;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class Recover {

    private long seq;

    private long cId;

    private String name;

    private String path;

    private String type;

    private String group;

    private String collection;

    private Timestamp uploadTime;
}
