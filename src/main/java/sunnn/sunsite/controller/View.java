package sunnn.sunsite.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class View {

    @GetMapping(value = "/hello")
    public String helloWorld(){
        return "helloworld";
    }

    @RequestMapping("home")
    public String home() {
        return "home";
    }

//    @RequestMapping("error")
//    public String error() {
//        return "error";
//    }

}
