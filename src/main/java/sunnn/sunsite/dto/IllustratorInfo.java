package sunnn.sunsite.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class IllustratorInfo {

    String illustrator;

    int post;
}
