package sunnn.sunsite.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import sunnn.sunsite.util.FileCache;
import sunnn.sunsite.util.SunSiteProperties;

import javax.servlet.MultipartConfigElement;
import javax.sql.DataSource;

@Component
@DependsOn("sunSiteProperties")
public class BeanConfiguration {

    @Bean
    @Scope("singleton")
    public FileCache fileCache() {
        return new FileCache(SunSiteProperties.cacheTimeout);
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("192MB");
        factory.setMaxRequestSize("256MB");
        return factory.createMultipartConfig();
    }
}
