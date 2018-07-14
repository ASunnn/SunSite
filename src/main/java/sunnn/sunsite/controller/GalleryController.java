package sunnn.sunsite.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sunnn.sunsite.dto.StatusCode;
import sunnn.sunsite.dto.request.PicInfo;
import sunnn.sunsite.dto.response.BaseResponse;
import sunnn.sunsite.dto.response.FileUploadResponse;
import sunnn.sunsite.service.GalleryService;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;

@Controller
public class GalleryController {

    @Autowired
    private GalleryService galleryService;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse upload(@RequestParam("file") MultipartFile[] files, HttpSession session) {
                //notice:RequestParam里的值需要与页面内的id一致
        if(files.length == 0)
            return new FileUploadResponse(StatusCode.EMPTY_UPLOAD);
        ArrayList<String> duplicatedFile = new ArrayList<>();
        for(MultipartFile file : files) {
            StatusCode s = galleryService.checkFile(file, session);
            if (s == StatusCode.EMPTY_UPLOAD)
                return new FileUploadResponse(s);
            else if (s == StatusCode.DUPLICATED_FILENAME)
                duplicatedFile.add(file.getOriginalFilename());
        }

        if(duplicatedFile.isEmpty())
            return new FileUploadResponse(StatusCode.OJBK);
        else
            return new FileUploadResponse(StatusCode.DUPLICATED_FILENAME,
                    (String[])duplicatedFile.toArray());
    }

    @RequestMapping(value = "/uploadInfo", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse upload(@RequestBody PicInfo info, HttpSession session) {
        if (info == null)
            return new BaseResponse(StatusCode.EMPTY_UPLOAD);
        galleryService.checkInfo(info);
        if(galleryService.saveUpload(session))
            return new BaseResponse(StatusCode.OJBK);
        else
            return new BaseResponse(StatusCode.ERROR);
    }

}
