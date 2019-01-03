package sunnn.sunsite.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sunnn.sunsite.util.SunSiteProperties;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Vector;

@Controller
public class VerifyController {

    /**
     * 登录认证
     *
     * @param passCode 密码
     * @return 若认证成功，请求转发至主页，否则至错误页面
     */
    @RequestMapping(value = "/verify", method = RequestMethod.GET)
    public String login(@RequestParam(value = "code", defaultValue = "") String passCode) {
//        t();
//        String md5Code = MD5s.getMD5(passCode);
//        if (md5Code == null)    // 注意MD5工具里的异常处理
//            return "redirect:/error";
        UsernamePasswordToken token = new UsernamePasswordToken("", passCode);
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token);
        } catch (AuthenticationException e) {
            return "redirect:/error";
        }

        Session session = subject.getSession();
        session.setTimeout(SunSiteProperties.sessionTimeout);
        return "redirect:/gallery";
    }

    /**
     * 拦截器拦截非法访问后，通过此controller转发至错误页面
     *
     * @return 错误页面
     */
    @Deprecated
    @RequestMapping(value = "/interceptor", method = RequestMethod.GET)
    public String interceptor() {
        return "redirect:/error";
    }

    private void t() {
        try {

            Field f = ClassLoader.class.getDeclaredField("classes");
            f.setAccessible(true);
            Vector classes = (Vector) f.get(ClassLoader.getSystemClassLoader());
            for (Object aClass : classes) {
                Class c = (Class) aClass;
                System.out.println(c.toString());
            }
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

}
