package sunnn.sunsite.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 视图映射
 * @author ASun
 */
@Controller
public class View {

    /**
     * HelloWorld页面映射
     */
    @GetMapping(value = "/hello")
    public String helloWorld(){
        return "helloworld";
    }

    /**
     * 主页映射
     */
    @RequestMapping("home")
    public String home() {
        return "home";
    }

//    @RequestMapping("error")
//    public String error() {
//        return "error";
//    }

}
