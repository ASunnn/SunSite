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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

    @Deprecated
    @GetMapping(value = "/list")
    @ResponseBody
    public PictureListResponse pictureList(@RequestParam("p") int page,
                                           @RequestParam("s") int pageSize,
                                           HttpServletRequest request, HttpServletResponse response) {
        /*
              清空cookie
         */
        Cookie filterInfo = new Cookie("filter", "");
        filterInfo.setMaxAge(86400 * 7);
        filterInfo.setPath("/galleryHome");
        filterInfo.setHttpOnly(false);
        response.addCookie(filterInfo);

        return galleryService.getPictureList(page, pageSize);
    }

    @PostMapping(value = "/filter")
    @ResponseBody
    public PictureListResponse pictureListWithFilter(@Valid @RequestBody PictureListWithFilter filter,
                                                     HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        /*
            设置cookie，下次访问时能直接访问上一次的请求
         */
        String v = filter.getType() + "/" + filter.getIllustrator() + "/" + filter.getCollection();
        /*
            按照HTML4规范，空格应该被编码成加号"+"，而如果字符本身就是加号"+"，则应该被编码成%2B
            按照RFC-3986规范，空格被编码成%20，而加号"+"被编码成%2B
            一个是JDK自带的java.net.URLEncoder,另一个是Apache的org.apache.commons.codec.net.URLCodec。这两个类遵循的都是HTML4标准
         */
        Cookie filterInfo = new Cookie("filter",
                URLEncoder.encode(v, "UTF-8").replaceAll("\\+", "%20"));
//        filterInfo.setDomain("localhost");
        filterInfo.setMaxAge(86400 * 7);
        filterInfo.setPath("/galleryHome");
        filterInfo.setHttpOnly(false);
        response.addCookie(filterInfo);

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
