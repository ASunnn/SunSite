package sunnn.sunsite.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import sunnn.sunsite.filter.RequestFilter;

@Component
public class FilterConfiguration {

    @Bean
    public RequestFilter requestFilter() {
        return new RequestFilter();
    }

}
