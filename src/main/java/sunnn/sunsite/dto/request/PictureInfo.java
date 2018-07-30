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

    @NotBlank
    private String illustrator;

    @NotBlank
    private String collection;

    @NotBlank
    private String type;

    private String uploadCode;
}
