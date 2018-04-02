package sunnn.sunsite.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("sunnn.sunsite.dao")
public class DataSourceConfiguration {

    @Value("${spring.datasource.dbcp2.driver-class-name}")
    private String diver;

    @Value("${spring.datasource.dbcp2.url}")
    private String url;

    @Value("${spring.datasource.dbcp2.username}")
    private String user;

    @Value("${spring.datasource.dbcp2.password}")
    private String passwd;

    @Value("${spring.datasource.dbcp2.max-wait-millis}")
    private long maxWaitTime;


    @Bean(name = "dataSource", destroyMethod = "close")
    public BasicDataSource createDataSource(){
        BasicDataSource dataSource = new BasicDataSource();

        dataSource.setDriverClassName(diver);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(passwd);
        dataSource.setMaxWaitMillis(maxWaitTime);

        return dataSource;
    }

}
