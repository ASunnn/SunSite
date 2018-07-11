package sunnn.sunsite.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Controller
public class GalleryController {

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
//    public String upload(MultipartFile file) {
    public String upload(@RequestParam("file") MultipartFile[] files) {
                //notice:RequestParam里的值需要与页面内的id一致

        for(MultipartFile file : files) {
            if(file == null || file.isEmpty())
                return "redirect:/error";

            File dir = new File("D:\\" + file.getOriginalFilename());
            try {
                file.transferTo(dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return "redirect:/home";

    }

}
