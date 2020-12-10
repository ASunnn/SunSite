package sunnn.sunsite.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
public class ModifyPicture {

    @NotBlank
    private String sequence;

    @NotBlank
    private String name;

    private String[] illustrators;

//    @NotBlank
//    private String group;
//
//    @NotBlank
//    private String collection;
}
