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
import sunnn.sunsite.dto.request.DeleteRequest;
import sunnn.sunsite.dto.request.GalleryInfo;
import sunnn.sunsite.dto.request.PictureListWithFilter;
import sunnn.sunsite.dto.response.*;
import sunnn.sunsite.exception.IllegalFileRequestException;
import sunnn.sunsite.util.StatusCode;
import sunnn.sunsite.dto.request.UploadPictureInfo;
import sunnn.sunsite.service.GalleryService;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    public PictureListResponse pictureList(@RequestParam("p") int page,
                                           @RequestParam("s") int pageSize) {
        return galleryService.getPictureList(page, pageSize);
    }

    @PostMapping(value = "/filter")
    @ResponseBody
    public PictureListResponse pictureListWithFilter(@Valid @RequestBody PictureListWithFilter filter) {
        return galleryService.getPictureListWithFilter(filter);
    }

    @GetMapping(value = "/m")
    public ResponseEntity thumbnailFile(@RequestParam("thumbnail") String sequenceCode) throws IOException {
        File file = galleryService.getThumbnail(Long.valueOf(sequenceCode));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
//        String destFileName = new String(
//                file.getName().getBytes("utf-8"), "iso8859-1");
//        //这货是用来告诉浏览器：此资源是用来下载的
//        headers.setContentDispositionFormData("attachment", destFileName);
        return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
    }

    @GetMapping(value = "/l/{illustrator}/{collection}/{pictureName}")
    public ResponseEntity pictureFile(@PathVariable("illustrator") String illustrator,
                                      @PathVariable("collection") String collection,
                                      @PathVariable("pictureName") String pictureName)
            throws IllegalFileRequestException, IOException {

        File file = galleryService.getPictureFile(illustrator, collection, pictureName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
    }

    @GetMapping(value = "/pictureInfo")
    @ResponseBody
    public PictureInfoResponse pictureInfo(@RequestParam("p") String sequenceCode, @RequestParam("e") boolean extra) {
        return galleryService.getPictureInfo(Long.valueOf(sequenceCode), extra);
    }

    @PostMapping(value = "/galleryInfo")
    @ResponseBody
    public GalleryInfoResponse galleryInfo(@Valid @RequestBody GalleryInfo galleryInfo) {
        /*
            生成查询
         */
        List<String> illustrators = new ArrayList<>();
        illustrators.add(galleryInfo.getIllustrator());
        List<String> collections = new ArrayList<>();
        collections.add(galleryInfo.getCollection());
        List<String> types = new ArrayList<>();
        types.add(galleryInfo.getType());
        /*
            查询
         */
        galleryService.getGalleryInfo(illustrators, collections, types);
        /*
            转换、返回结果
         */
        return new GalleryInfoResponse(StatusCode.OJBK)
                .setIllustrators(illustrators)
                .setCollections(collections)
                .setTypes(types);
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
        for (MultipartFile file : files) {
            StatusCode s = galleryService.checkFile(file, uploadCode);
            //非法文件，直接返回错误
            if (s != StatusCode.OJBK)
                return new FileUploadResponse(s);
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
                galleryService.saveUpload(info));
    }

    @PostMapping(value = "/delete")
    @ResponseBody
    public BaseResponse deletePicture(@Valid @RequestBody DeleteRequest deleteRequest) {
        long sequence;
        try {
            sequence = Long.valueOf(deleteRequest.getDeleteInfo());
        } catch (NumberFormatException e) {
            return new BaseResponse(StatusCode.ILLEGAL_DATA);
        }
        return new BaseResponse(galleryService.deletePicture(sequence));
    }
}
