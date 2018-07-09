package sunnn.sunsite.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Controller
public class GalleryController {

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String upload(MultipartFile file) {

        if(file == null || file.isEmpty())
            return "redirect:/error";

        File dir = new File("D:\\t.png");
        try {
            file.transferTo(dir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/home";

    }

}
