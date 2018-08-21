package sunnn.sunsite.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

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

    /**
     * 图片显示
     */
    @RequestMapping(value = "/gallery/show/*")
    public String gallery() {
        return "gallery";
    }

    /**
     * 画师显示
     */
    @RequestMapping(value = "/illustrator/show/*")
    public String illustrator() {
        return "illustrator";
    }

    /**
     * 画册显示
     */
    @RequestMapping(value = "/collection/show/*")
    public String collection() {
        return "collection";
    }


}
