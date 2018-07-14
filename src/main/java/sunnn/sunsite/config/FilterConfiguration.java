package sunnn.sunsite.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sunnn.sunsite.filter.RequestFilter;

@Configuration
public class FilterConfiguration {

    @Bean
    public RequestFilter requestFilter() {
        return new RequestFilter();
    }

}
