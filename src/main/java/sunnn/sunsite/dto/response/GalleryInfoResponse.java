package sunnn.sunsite.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sunnn.sunsite.util.Convertible;
import sunnn.sunsite.util.StatusCode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class GalleryInfoResponse<T> extends BaseResponse implements Convertible<GalleryInfoResponse, T> {

    private static Logger log = LoggerFactory.getLogger(GalleryInfoResponse.class);

    private String[] dataList;

    public GalleryInfoResponse(StatusCode statusCode) {
        super(statusCode);
    }

    public GalleryInfoResponse(StatusCode statusCode, String[] dataList) {
        super(statusCode);
        this.dataList = dataList;
    }

    @Override
    public GalleryInfoResponse convertTo(List<T> entities) {
        if (entities.isEmpty())
            return this;

        String[] dataList = new String[entities.size()];
        try {
            Class clazz = entities.get(0).getClass();
            @SuppressWarnings("unchecked")
            Method m = clazz.getDeclaredMethod("getName");

            for (int i = 0; i < entities.size(); ++i)
                dataList[i] = (String) m.invoke(entities.get(i));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("Error At Convert Response : ", e);
            return new GalleryInfoResponse(StatusCode.ERROR);
        }
        return setDataList(dataList);
    }

}
