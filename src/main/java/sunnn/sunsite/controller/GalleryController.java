package sunnn.sunsite.controller;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sunnn.sunsite.util.StatusCode;
import sunnn.sunsite.dto.request.PictureInfo;
import sunnn.sunsite.dto.response.BaseResponse;
import sunnn.sunsite.dto.response.FileUploadResponse;
import sunnn.sunsite.service.GalleryService;

import javax.validation.Valid;
import java.util.ArrayList;

@Controller
public class GalleryController {

    private final GalleryService galleryService;

    @Autowired
    public GalleryController(GalleryService galleryService) {
        this.galleryService = galleryService;
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse upload(@RequestParam("file") MultipartFile[] files) {
        //notice:RequestParam里的值需要与页面内的id一致
        /*
            检查是否为空上传
         */
        if (files.length == 0)
            return new FileUploadResponse(StatusCode.EMPTY_UPLOAD);
        /*
            生成唯一的上传id：当前时间 + 用户id
         */
        String uploadCode = String.valueOf(System.currentTimeMillis())
                + SecurityUtils.getSubject().getSession().getId();
        /*
            对上传的文件进行逐个处理
         */
        ArrayList<String> duplicatedFile = new ArrayList<>();
        for (MultipartFile file : files) {
            StatusCode s = galleryService.checkFile(file, uploadCode);
            //非法文件，直接返回错误
            if (s == StatusCode.ILLEGAL_DATA)
                return new FileUploadResponse(s);
                //重复文件
            else if (s == StatusCode.DUPLICATED_FILENAME)
                duplicatedFile.add(file.getOriginalFilename());
        }
        /*
            根据是否有重复文件判断结果
         */
        if (duplicatedFile.isEmpty())
            return new FileUploadResponse(StatusCode.OJBK, uploadCode);
        else {
            String[] dFile = new String[duplicatedFile.size()];
            duplicatedFile.toArray(dFile);
            return new FileUploadResponse(StatusCode.DUPLICATED_FILENAME, dFile);
        }
    }

    @RequestMapping(value = "/uploadInfo", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse upload(@Valid @RequestBody PictureInfo info) {
        /*
            保存上传
         */
        //TODO 业务流程具备一定的事务能力
        if (galleryService.saveUpload(info))
            return new BaseResponse(StatusCode.OJBK);
        else
            return new BaseResponse(StatusCode.ERROR);
    }

}
