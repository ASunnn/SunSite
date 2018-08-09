package sunnn.sunsite.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import sunnn.sunsite.exception.IllegalFileRequestException;
import sunnn.sunsite.util.StatusCode;
import sunnn.sunsite.dto.response.BaseResponse;

/**
 * 统一异常处理
 */
@ControllerAdvice
public class GlobalExceptionResolver {

    private static Logger log = LoggerFactory.getLogger(GlobalExceptionResolver.class);

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public BaseResponse argumentNotValidExceptionResolver() {
        log.warn("Method Argument Not Valid");
        return new BaseResponse(StatusCode.ILLEGAL_DATA);
    }

    @ExceptionHandler(value = IllegalFileRequestException.class)
    public String illegalFileResolver(Exception e) {
        log.warn("Illegal File Request : " + e);
        return "error";
    }

}
