package sunnn.sunsite.controller;

import org.apache.commons.io.FileUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sunnn.sunsite.dto.request.DeletePicture;
import sunnn.sunsite.dto.response.PictureInfoResponse;
import sunnn.sunsite.dto.response.PictureListResponse;
import sunnn.sunsite.entity.Picture;
import sunnn.sunsite.util.StatusCode;
import sunnn.sunsite.dto.request.UploadPictureInfo;
import sunnn.sunsite.dto.response.BaseResponse;
import sunnn.sunsite.dto.response.FileUploadResponse;
import sunnn.sunsite.service.GalleryService;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@Controller
@RequestMapping("/gallery")
public class GalleryController {

    private final GalleryService galleryService;

    @Autowired
    public GalleryController(GalleryService galleryService) {
        this.galleryService = galleryService;
    }

    @GetMapping(value = "/list")
    @ResponseBody
    public PictureListResponse getPictureList(@RequestParam("p") int page,
                                              @RequestParam("s") int pageSize) {
        return galleryService.getPictureList(page, pageSize);
    }

    @GetMapping(value = "/m")
    public ResponseEntity getThumbnail(@RequestParam("thumbnail") String pictureName) throws IOException {
        File file = galleryService.getThumbnail(pictureName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
//        String destFileName = new String(
//                file.getName().getBytes("utf-8"), "iso8859-1");
        //这货是用来告诉浏览器：此资源是用来下载的
//        headers.setContentDispositionFormData("attachment", destFileName);
        return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
    }

    @GetMapping(value = "/pictureInfo")
    @ResponseBody
    public PictureInfoResponse getPictureInfo(@RequestParam("p") String pictureName, @RequestParam("e") boolean extra) {
        return galleryService.getPictureInfo(pictureName, extra);
    }

    @GetMapping(value = "/l/{name}")
    public ResponseEntity getPictureFile(@PathVariable("name") String pictureName) throws IOException {
        File file = galleryService.getPictureFile(pictureName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
    }

    @PostMapping(value = "/upload")
    @ResponseBody
    public FileUploadResponse upload(@RequestParam("file") MultipartFile[] files) {
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

    @PostMapping(value = "/uploadInfo")
    @ResponseBody
    public BaseResponse upload(@Valid @RequestBody UploadPictureInfo info) {
        /*
            保存上传
         */
        return new BaseResponse(
                galleryService.saveUpload(info));
    }

    @PostMapping(value = "/delete")
    @ResponseBody
    public BaseResponse deletePicture(@Valid @RequestBody DeletePicture deletePicture) {
        return new BaseResponse(
                galleryService.deletePicture(
                        deletePicture.getPictureName()));
    }
}
