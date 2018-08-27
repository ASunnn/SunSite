package sunnn.sunsite.dto;

import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
public class AggregationRes {

    @Field("field")
    private String field;


    @Field("count")
    private long count;

    @Override
    public String toString() {
        return "AggregationRes{" +
                "field='" + field + '\'' +
                ", count=" + count +
                '}';
    }
}
