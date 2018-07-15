package sunnn.sunsite.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 视图映射
 *
 * @author ASun
 */
@Controller
public class View {

    /**
     * 主页
     */
    @RequestMapping(value = "/index")
    public String index() {
        return "index";
    }

    /**
     * HelloWorld页面
     */
    @RequestMapping(value = "/hello")
    public String helloWorld() {
        return "helloWorld";
    }

    /**
     * 主页
     */
    @RequestMapping(value = "/home")
    public String home() {
        return "home";
    }

    /**
     * 画册主页
     */
    @RequestMapping(value = "/galleryHome")
    public String galleryHome() {
        return "galleryHome";
    }

}
