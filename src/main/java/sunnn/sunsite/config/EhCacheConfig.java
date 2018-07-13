package sunnn.sunsite.config;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class EhCacheConfig extends CacheConfiguration {
    @Bean
    public EhCacheManagerFactoryBean cacheManagerFactoryBean() {
        EhCacheManagerFactoryBean bean = new EhCacheManagerFactoryBean();
        bean.setConfigLocation(new ClassPathResource("ehcache.xml"));
        bean.setShared(true);
        return bean;
    }

//    @Bean
//    public CacheManager ehCacheCacheManager(EhCacheManagerFactoryBean bean) {
//        return new EhCacheCacheManager(bean.getObject()).getCacheManager();
//    }

}
