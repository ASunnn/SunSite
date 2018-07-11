package sunnn.sunsite.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sunnn.sunsite.message.StatusCode;
import sunnn.sunsite.message.request.PicInfo;
import sunnn.sunsite.message.response.BaseResponse;

import java.io.File;
import java.io.IOException;

@Controller
public class GalleryController {

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse upload(@RequestParam("file") MultipartFile[] files) {
                //notice:RequestParam里的值需要与页面内的id一致
        for(MultipartFile file : files) {
            if(file == null || file.isEmpty())
                return new BaseResponse(StatusCode.UPLOAD_ERROR);

            File dir = new File("D:\\" + file.getOriginalFilename());
            try {
                file.transferTo(dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new BaseResponse(StatusCode.OJBK);
    }

    @RequestMapping(value = "/uploadInfo", method = RequestMethod.POST)
    @ResponseBody
    public String upload(@RequestBody PicInfo info) {
        if (info == null)
            return "redirect:/error";
        System.out.println(info.toString());
        return "redirect:/home";
    }

}
