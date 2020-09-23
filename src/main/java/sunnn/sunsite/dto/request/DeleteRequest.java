package sunnn.sunsite.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@Deprecated
public class DeleteRequest {

    @NotBlank
    private String deleteInfo;

}
