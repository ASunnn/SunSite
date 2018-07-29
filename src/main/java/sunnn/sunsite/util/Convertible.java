package sunnn.sunsite.util;

import java.util.List;

public interface Convertible<T, E> {

    T convertTo(List<E> entity);

}
