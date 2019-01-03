package sunnn.sunsite.config;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
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
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
//        sqlSessionFactoryBean.setMapperLocations(
//                new ClassPathResource[]{
//                        new ClassPathResource("/mapper/PicMapper.xml"),
//                        new ClassPathResource("/mapper/PictureMapper.xml"),
//                        new ClassPathResource("/mapper/CollectionMapper.xml"),
//                        new ClassPathResource("/mapper/GroupMapper.xml"),
//                        new ClassPathResource("/mapper/TypeMapper.xml"),
//                        new ClassPathResource("/mapper/IllustratorMapper.xml"),});
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:/mapper/*.xml");
        sqlSessionFactoryBean.setMapperLocations(resources);
        return sqlSessionFactoryBean.getObject();
    }

    @Bean
    public DataSource dataSource() {
//        HikariDataSource dataSource = DataSourceBuilder.create().type(HikariDataSource.class).build();
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setJdbcUrl(parseJdbcUrl());
        dataSource.setUsername(SunSiteProperties.username);
        dataSource.setPassword(SunSiteProperties.password);

        return dataSource;
    }

    private String parseJdbcUrl() {
        return "jdbc:mysql://"
                + SunSiteProperties.host
                + "/" + SunSiteProperties.database
                + "?characterEncoding=utf8&useSSL=false";
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("192MB");
        factory.setMaxRequestSize("256MB");
        return factory.createMultipartConfig();
    }
}
