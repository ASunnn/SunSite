package sunnn.sunsite.dto;

import java.util.List;

public interface Convertible<T, E> {

    T convertTo(List<E> entity);

}
