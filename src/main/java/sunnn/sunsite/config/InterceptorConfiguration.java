package sunnn.sunsite.config;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import sunnn.sunsite.interceptor.RequestInterceptor;
import sunnn.sunsite.interceptor.VerifyInterceptor;

/**
 * 拦截器配置
 * @author ASun
 */
@Configuration
public class InterceptorConfiguration  extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //访问拦截器
        InterceptorRegistration requestInterceptor = registry.addInterceptor(new RequestInterceptor());
        //拦截路径
        requestInterceptor.addPathPatterns("/**");

        //认证拦截器
        InterceptorRegistration verifyInterceptor = registry.addInterceptor(new VerifyInterceptor());
        verifyInterceptor.addPathPatterns("/**");
        //主页、helloworld、认证、拦截、错误请求
        verifyInterceptor.excludePathPatterns("/index.html", "/hello", "/verify", "/interceptor", "/error");
    }
}
