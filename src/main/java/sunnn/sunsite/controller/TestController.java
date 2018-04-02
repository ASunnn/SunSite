package sunnn.sunsite.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class TestController {

    @GetMapping(value = "/hello")
    public String helloWorld(){
        return "index";
    }

}
