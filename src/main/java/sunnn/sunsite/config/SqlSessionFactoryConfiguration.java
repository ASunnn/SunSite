package sunnn.sunsite.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;

@Configuration
public class SqlSessionFactoryConfiguration {

    @Value("${mybatis.config-location}")
    private String mybatisConfig;

    @Value("${mybatis.mapper-locations}")
    private String mybatisMapper;

    private final BasicDataSource dataSource;

    @Autowired
    public SqlSessionFactoryConfiguration(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactoryBean createSqlSessionFactory() throws IOException {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();

        sessionFactoryBean.setConfigLocation(new ClassPathResource(mybatisConfig));
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        String path = PathMatchingResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + mybatisMapper;
        sessionFactoryBean.setMapperLocations(resolver.getResources(path));
        sessionFactoryBean.setDataSource(dataSource);
        String entityPackage = "sunnn.sunsite.entity";
        sessionFactoryBean.setTypeAliasesPackage(entityPackage);

        return sessionFactoryBean;
    }

}
