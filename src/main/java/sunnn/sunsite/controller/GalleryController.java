package sunnn.sunsite.controller;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sunnn.sunsite.dto.request.DeleteRequest;
import sunnn.sunsite.dto.request.UploadPictureInfo;
import sunnn.sunsite.dto.response.BaseResponse;
import sunnn.sunsite.dto.response.FileUploadResponse;
import sunnn.sunsite.dto.response.PictureInfoResponse;
import sunnn.sunsite.dto.response.PictureListResponse;
import sunnn.sunsite.service.GalleryService;
import sunnn.sunsite.service.PictureService;
import sunnn.sunsite.util.StatusCode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/gallery")
public class GalleryController {

    private final GalleryService galleryService;

    private final PictureService pictureService;

    @Autowired
    public GalleryController(GalleryService galleryService, PictureService pictureService) {
        this.galleryService = galleryService;
        this.pictureService = pictureService;
    }

    @GetMapping(value = "/list")
    @ResponseBody
    public PictureListResponse pictureList(@RequestParam("p") int page,
                                           HttpServletRequest request, HttpServletResponse response) {
        return galleryService.getPictureList(page);
    }

    @GetMapping(value = "/info")
    @ResponseBody
    public PictureInfoResponse pictureDetail(@RequestParam("seq") long sequence) {
        return galleryService.getPictureInfo(sequence);
    }

    @PostMapping(value = "/upload")
    @ResponseBody
    public FileUploadResponse upload(@RequestParam("file") MultipartFile[] files) {
        // notice:RequestParam里的值需要与页面内的id一致
        /*
            检查是否为空上传
         */
        if (files.length == 0)
            return new FileUploadResponse(StatusCode.ILLEGAL_INPUT);
        /*
            生成唯一的上传id：当前时间 + 用户id
         */
        String uploadCode = String.valueOf(System.currentTimeMillis())
                + SecurityUtils.getSubject().getSession().getId();
        /*
            对上传的文件进行逐个处理
         */
        for (MultipartFile file : files) {
            StatusCode code = pictureService.uploadPicture(file, uploadCode);
            // 非法文件，直接返回错误
            if (code != StatusCode.OJBK)
                return new FileUploadResponse(code);
        }

        return new FileUploadResponse(StatusCode.OJBK, uploadCode);
    }

    @PostMapping(value = "/uploadInfo")
    @ResponseBody
    public BaseResponse upload(@Valid @RequestBody UploadPictureInfo info) {
        /*
            保存上传
         */
        return new BaseResponse(
                pictureService.uploadInfoAndSave(info));
    }

    @PostMapping(value = "/delete")
    @ResponseBody
    public BaseResponse deletePicture(@Valid @RequestBody DeleteRequest info) {
        return new BaseResponse(
                pictureService.delete(Long.valueOf(info.getDeleteInfo())));
    }
}
