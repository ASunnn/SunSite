package sunnn.sunsite.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 图片-绘师
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class Artwork {

    private int illustrator;

    private long sequence;
}
