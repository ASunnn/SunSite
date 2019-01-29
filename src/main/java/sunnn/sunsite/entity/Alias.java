package sunnn.sunsite.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 社团/绘师别名
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class Alias {

    private String alias;

    private int origin;

    private int kind;

    public static final int ILLUSTRATOR = 0;

    public static final int GROUP = 1;
}
