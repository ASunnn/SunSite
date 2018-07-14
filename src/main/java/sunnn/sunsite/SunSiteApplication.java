package sunnn.sunsite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SunSiteApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(SunSiteApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SunSiteApplication.class);
	}

}

//TODO 使用shiro进行登录管理

//TODO 使用filter进行访问记录
