package sunnn.sunsite.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import sunnn.sunsite.util.FileCache;

@Component
public class BeanConfiguration {

    @Bean
    @Scope("singleton")
    public FileCache fileCache() {
        return new FileCache();
    }

//    @Bean
//    @Scope("singleton")
//    public SunSiteConfiguration sunSiteConfiguration() throws IOException {
//        return new SunSiteConfiguration();
//    }

}
