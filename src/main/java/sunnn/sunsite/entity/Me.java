package sunnn.sunsite.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 用于个人验证的实体类
 * @author ASun
 */
@Document(collection = "me")
@Getter
@Setter
@AllArgsConstructor
public class Me {

    /**
     * 验证密码
     */
    @Field(value = "passCode")
    private String passCode;
}
