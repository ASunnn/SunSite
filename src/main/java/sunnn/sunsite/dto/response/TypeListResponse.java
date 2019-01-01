package sunnn.sunsite.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import sunnn.sunsite.dto.Convertible;
import sunnn.sunsite.entity.Type;
import sunnn.sunsite.util.StatusCode;

import java.util.List;


@Getter
@Setter
@Accessors(chain = true)
@ToString
public class TypeListResponse extends BaseResponse implements Convertible<TypeListResponse, Type> {

    private TypeListInfo[] typeList;

    public TypeListResponse(StatusCode statusCode) {
        super(statusCode);
    }

    @Override
    public TypeListResponse convertTo(List<Type> entity) {
        if (entity.isEmpty())
            return this;

        TypeListInfo[] typeList = new TypeListInfo[entity.size()];
        for (int i = 0; i < entity.size(); ++i) {
            TypeListInfo info = new TypeListInfo();
            info.type = entity.get(i).getName();
            info.lastUpdate = entity.get(i).getLastUpdate().toString();
            typeList[i] = info;
        }

        return setTypeList(typeList);
    }
}

@Getter
@Setter
class TypeListInfo {

    String type;

    String lastUpdate;
}