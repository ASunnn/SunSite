//package sunnn.sunsite.aop;
//
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.AfterThrowing;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//import sunnn.sunsite.dto.request.UploadPictureInfo;
//
//@Component
//@Aspect
//public class GalleryAspect {
//
//    private static Logger log = LoggerFactory.getLogger(GalleryAspect.class);
//
//    @Pointcut("execution(* sunnn.sunsite.service.impl.GalleryServiceImpl.saveUpload(..))")
//    public void errorCatch() {
//    }
//
//    @AfterThrowing(value = "errorCatch()", throwing = "t")
//    public void getThrow(JoinPoint joinPoint, Throwable t) {
//        UploadPictureInfo info = (UploadPictureInfo) joinPoint.getArgs()[0];
//
//        log.warn(info.toString());
//
//        log.error("Error At SaveUpload : ", t);
//    }
//
//}
