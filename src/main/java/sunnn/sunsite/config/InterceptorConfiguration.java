package sunnn.sunsite.config;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import sunnn.sunsite.interceptor.RequestInterceptor;

@Configuration
public class InterceptorConfiguration  extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册拦截器
        InterceptorRegistration ir = registry.addInterceptor(new RequestInterceptor());
        // 配置拦截的路径
        ir.addPathPatterns("/**");

    }
}
