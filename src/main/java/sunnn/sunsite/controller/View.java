package sunnn.sunsite.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class View {

    @RequestMapping(value = "/login")
    public String login() {
        return "/index.html";
    }

    @RequestMapping(value = "/index")
    public String index() {
        return "/index.html";
    }

    @RequestMapping(value = "/gallery")
    public String gallery() {
        return "/index.html";
    }

    @RequestMapping(value = "/illustrator")
    public String illustrator() {
        return "/index.html";
    }

    @RequestMapping(value = "/work/*")
    public String work() {
        return "/index.html";
    }

    @RequestMapping(value = "/circle")
    public String group() {
        return "/index.html";
    }

    @RequestMapping(value = "/book/*")
    public String book() {
        return "/index.html";
    }

    @RequestMapping(value = "/collection")
    public String collection() {
        return "/index.html";
    }

    @RequestMapping(value = "/pool/*")
    public String pool() {
        return "/index.html";
    }

    @RequestMapping(value = "/type")
    public String type() {
        return "/index.html";
    }

    @RequestMapping(value = "/list/*")
    public String typeShow() {
        return "/index.html";
    }

    @RequestMapping(value = "/post/*")
    public String post() {
        return "/index.html";
    }

    @RequestMapping(value = "/create")
    public String create() {
        return "/index.html";
    }

    @RequestMapping(value = "/upload")
    public String upload() {
        return "/index.html";
    }
}
