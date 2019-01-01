package sunnn.sunsite.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 一张图片最基本的预览信息
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class PictureBase {

    private long sequence;

    private String name;

    private String group;

    private long cId;

    private String collection;
}
