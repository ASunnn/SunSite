package sunnn.sunsite.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import sunnn.sunsite.dto.Convertible;
import sunnn.sunsite.dto.GroupInfo;
import sunnn.sunsite.util.StatusCode;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class GroupListResponse extends BaseResponse implements Convertible<GroupListResponse, GroupInfo> {

    private GroupListInfo[] groupList;

    private int pageCount;

    public GroupListResponse(StatusCode statusCode) {
        super(statusCode);
    }

    @Override
    public GroupListResponse convertTo(List<GroupInfo> entity) {
        if (entity.isEmpty())
            return this;

        GroupListInfo[] groupList = new GroupListInfo[entity.size()];
        for (int i = 0; i < entity.size(); ++i) {
            GroupListInfo info = new GroupListInfo();
            info.group = entity.get(i).getGroup();
            info.post = entity.get(i).getPost();
            info.lastUpdate = entity.get(i).getLastUpdate().toString();
            groupList[i] = info;
        }

        return setGroupList(groupList);
    }
}

@Getter
@Setter
class GroupListInfo {

    String group;

    int post;

    String lastUpdate;
}