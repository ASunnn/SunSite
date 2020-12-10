package sunnn.sunsite.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import sunnn.sunsite.dto.Convertible;
import sunnn.sunsite.dto.TypeInfo;
import sunnn.sunsite.entity.Type;
import sunnn.sunsite.util.StatusCode;

import java.util.List;


@Getter
@Setter
@Accessors(chain = true)
@ToString
public class TypeListResponse extends BaseResponse {

    private TypeInfo[] list;

    public TypeListResponse(StatusCode statusCode) {
        super(statusCode);
    }
}