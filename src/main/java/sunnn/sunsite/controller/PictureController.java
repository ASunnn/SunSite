package sunnn.sunsite.controller;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import sunnn.sunsite.dto.response.MsgResponse;
import sunnn.sunsite.dto.response.PictureInfoResponse;
import sunnn.sunsite.exception.IllegalFileRequestException;
import sunnn.sunsite.service.GalleryService;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Controller
public class PictureController {

    private final GalleryService galleryService;

    @Autowired
    public PictureController(GalleryService galleryService) {
        this.galleryService = galleryService;
    }

    @GetMapping(value = "/m/{sequence}")
    public ResponseEntity thumbnailFile(@PathVariable("sequence") long sequence) throws IOException {
        File file = galleryService.getThumbnail(sequence);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
//        String destFileName = new String(
//                file.getCollection().getBytes("utf-8"), "iso8859-1");
//        //这货是用来告诉浏览器：此资源是用来下载的
//        headers.setContentDispositionFormData("attachment", destFileName);
        return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
    }

    @GetMapping(value = "/p/{sequence}")
    public String pictureFile(@PathVariable("sequence") long sequence) throws UnsupportedEncodingException {
        PictureInfoResponse response = galleryService.getPictureInfo(sequence);
        /*
            按照HTML4规范，空格应该被编码成加号"+"，而如果字符本身就是加号"+"，则应该被编码成%2B
            按照RFC-3986规范，空格被编码成%20，而加号"+"被编码成%2B
            一个是JDK自带的java.net.URLEncoder,另一个是Apache的org.apache.commons.codec.net.URLCodec。这两个类遵循的都是HTML4标准
         */
        return "redirect:/p/"
                + response.getSequence()
                + "/" + URLEncoder.encode(response.getName(), "UTF-8").replaceAll("\\+", "%20");
    }

    @GetMapping(value = "/p/{sequence}/{name}")
    public ResponseEntity pictureFile(@PathVariable("sequence") long sequence, @PathVariable("name") String name)
            throws IllegalFileRequestException, IOException {
        File file = galleryService.getPictureFile(sequence);

        String fileName = file.getName();
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

        HttpHeaders headers = new HttpHeaders();
        switch (extension) {
            case "jpg":
            case "jpeg":
                headers.setContentType(MediaType.IMAGE_JPEG);
                break;
            case "png":
                headers.setContentType(MediaType.IMAGE_PNG);
                break;
        }
        return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
    }
}
