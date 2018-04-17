package sunnn.sunsite.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sunnn.sunsite.dao.MeDao;
import sunnn.sunsite.entity.Me;
import sunnn.sunsite.util.MD5s;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class VerifyController {

    @Autowired
    private MeDao meDao;

    @RequestMapping(value = "/verify", method = RequestMethod.GET)
    public String login(@RequestParam(value = "code", defaultValue = "")String passCode, HttpSession session) {

        if(passCode.equals(""))
            throw new RuntimeException();
        String md5Code = MD5s.getMD5(passCode);
//        String realCode = meDao.selectMe().getPassCode();
        String realCode = meDao.findAll(Me.class).get(0).getPassCode();
        if(md5Code.equals(realCode)) {
            session.setMaxInactiveInterval(60);
            session.setAttribute("pass", System.currentTimeMillis());
        } else {
            return "redirect:/error";
        }
        return "redirect:/home";
    }

    @RequestMapping(value = "/interceptor", method = RequestMethod.GET)
    public String interceptor(@RequestParam(value = "code", defaultValue = "") String passCode, HttpSession session) {

        return "redirect:/error";
    }

}
