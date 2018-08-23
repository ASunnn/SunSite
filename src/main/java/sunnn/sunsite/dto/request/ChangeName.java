package sunnn.sunsite.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
public class ChangeName {

    @NotBlank
    private String oldName;

    @NotBlank
    private String newName;

}
