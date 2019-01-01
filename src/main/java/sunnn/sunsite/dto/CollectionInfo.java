package sunnn.sunsite.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class CollectionInfo {

    long sequence;

    String group;

    String collection;

    String type;

    int post;

    Timestamp lastUpdate;
}
