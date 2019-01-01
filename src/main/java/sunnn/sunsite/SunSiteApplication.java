package sunnn.sunsite;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
//@MapperScan("sunnn.sunsite.mapper")
public class SunSiteApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(SunSiteApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SunSiteApplication.class);
    }

}