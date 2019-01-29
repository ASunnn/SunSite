package sunnn.sunsite.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 图片-标签
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class Tags {

    private int tag;

    private long sequence;
}
