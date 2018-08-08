package sunnn.sunsite.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChangeName {

    private String oldName;

    private String newName;

}
