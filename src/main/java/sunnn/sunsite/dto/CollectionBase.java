package sunnn.sunsite.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * 画集的基本信息
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class CollectionBase {

    @NotBlank
    private String collection;

    @NotBlank
    private String group;

    @NotBlank
    private String type;
}