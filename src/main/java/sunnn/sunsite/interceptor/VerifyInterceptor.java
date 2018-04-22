package sunnn.sunsite.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 认证拦截器
 * @author ASun
 */
public class VerifyInterceptor implements HandlerInterceptor {

    private static Logger log = LoggerFactory.getLogger(VerifyInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("Enter VerifyInterceptor : " + request.getRequestURI());
        if(request.getSession().getAttribute("pass") == null) {
            log.warn("No Session");
            request.getRequestDispatcher("/interceptor").forward(request, response);
            return false;
        }
        log.info("Pass VerifyInterceptor");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
