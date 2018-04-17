package sunnn.sunsite.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import sunnn.sunsite.interceptor.VerifyInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class GlobalExceptionResolver implements HandlerExceptionResolver {

    private static Logger log = LoggerFactory.getLogger(VerifyInterceptor.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {

        log.error("Enter GlobalExceptionResolver");
        ModelAndView mv = new ModelAndView("error");
        return mv;
    }
}
