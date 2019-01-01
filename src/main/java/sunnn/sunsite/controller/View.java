package sunnn.sunsite.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 视图映射
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
    @RequestMapping(value = "/gallery")
    public String gallery() {
        return "gallery";
    }

    /**
     * 图片页
     */
    @RequestMapping(value = "/gallery/show/*")
    public String galleryShow() {
        return "post";
    }

    /**
     * 社团
     */
    @RequestMapping(value = "/group")
    public String group() {
        return "group";
    }

    /**
     * 社团
     */
    @RequestMapping(value = "/group/show/*")
    public String groupShow() {
        return "work";
    }

    /**
     * 画集
     */
    @RequestMapping(value = "/collection")
    public String collection() {
        return "collection";
    }

    /**
     * 画集页
     */
    @RequestMapping(value = "/collection/show/*")
    public String collectionShow() {
        return "pool";
    }

    /**
     * 类型
     */
    @RequestMapping(value = "/type")
    public String type() {
        return "type";
    }

    /**
     * 类型页
     */
    @RequestMapping(value = "/type/show/*")
    public String typeShow() {
        return "list";
    }

    /**
     * 创建画集页
     */
    @RequestMapping(value = "/create")
    public String create() {
        return "create";
    }

    /**
     * 画师
     */
    @RequestMapping(value = "/illustrator")
    public String illustrator() {
        return "illustrator";
    }

    /**
     * 画师页
     */
    @RequestMapping(value = "/illustrator/show/*")
    public String illustratorShow() {
        return "artwork";
    }

    /**
     * 上传图片页
     */
    @RequestMapping(value = "/upload")
    public String upload() {
        return "upload";
    }

//
//    /**
//     * 图片显示
//     */
//    @RequestMapping(value = "/gallery/show/*")
//    public String gallery() {
//        return "gallery";
//    }
//
//    /**
//     * 画师显示
//     */
//    @RequestMapping(value = "/illustrators/show/*")
//    public String illustrators() {
//        return "illustrators";
//    }
//
//    /**
//     * 画册显示
//     */
//    @RequestMapping(value = "/collection/show/*")
//    public String collection() {
//        return "collection";
//    }
//
//    /**
//     * Pool显示
//     */
//    @RequestMapping(value = "/pool/show/**")
//    public String pool() {
//        return "pool";
//    }

}
