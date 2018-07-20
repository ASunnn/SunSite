package sunnn.sunsite.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PictureInfo {

    @NotBlank(message = "Illustrator Can't Be Empty")
    private String illustrator;

    @NotBlank(message = "Collection Can't Be Empty")
    private String collection;

    @NotBlank(message = "Type Can't Be Empty")
    private String type;

    private String uploadCode;
}
