package sunnn.sunsite.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 用于个人验证的实体类
 * @author ASun
 */
@Document(collection = "me")
public class Me {

    /**
     * 验证密码
     */
    @Field(value = "passcode")
    private String passCode;

    public Me(String passCode) {
        this.passCode = passCode;
    }

    public String getPassCode() {
        return passCode;
    }

    public void setPassCode(String passCode) {
        this.passCode = passCode;
    }
}
