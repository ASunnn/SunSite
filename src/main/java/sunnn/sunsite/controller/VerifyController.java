package sunnn.sunsite.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sunnn.sunsite.dao.MeDao;
import sunnn.sunsite.dto.FileCache;
import sunnn.sunsite.entity.Me;
import sunnn.sunsite.util.MD5s;

import javax.servlet.http.HttpSession;

/**
 * 登录认证控制层
 * @author ASun
 */
@Controller
public class VerifyController {

    private final MeDao meDao;

    @Autowired
    public VerifyController(MeDao meDao) {
        this.meDao = meDao;
    }

    /**
     * 登录认证
     * @param passCode  密码
     * @param session   session
     * @return  若认证成功，请求转发至主页，否则至错误页面
     */
    @RequestMapping(value = "/verify", method = RequestMethod.GET)
    public String login(@RequestParam(value = "code", defaultValue = "")String passCode, HttpSession session) {
        //for test
        if(passCode.equals(""))
            throw new RuntimeException();

        String md5Code = MD5s.getMD5(passCode);
        String realCode = meDao.findAll(Me.class).get(0).getPassCode();

        if(md5Code.equals(realCode)) {
            session.setMaxInactiveInterval(600);
            session.setAttribute("pass", System.currentTimeMillis());
        } else {
            return "redirect:/error";
        }
        return "redirect:/home";
    }

    /**
     * 拦截器拦截非法访问后，通过此controller转发至错误页面
     * @return  错误页面
     */
    @RequestMapping(value = "/interceptor", method = RequestMethod.GET)
    public String interceptor() {
        return "redirect:/error";
    }

}
