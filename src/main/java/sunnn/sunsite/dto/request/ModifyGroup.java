package sunnn.sunsite.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
public class ModifyGroup {

    @NotBlank
    private String group;

    @NotBlank
    private String newName;

    private String aliases;
}
