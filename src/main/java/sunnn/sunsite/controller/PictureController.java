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
import sunnn.sunsite.exception.IllegalFileRequestException;
import sunnn.sunsite.service.GalleryService;

import java.io.File;
import java.io.IOException;

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
    public ResponseEntity pictureFile(@PathVariable("sequence") long sequence)
            throws IllegalFileRequestException, IOException {
        File file = galleryService.getPictureFile(sequence);

        String fileName = file.getName();
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1);

        HttpHeaders headers = new HttpHeaders();
        switch (extension) {
            case "jpg":
            case "jpeg":
                headers.setContentType(MediaType.IMAGE_JPEG);
                break;
            case "png":
                headers.setContentType(MediaType.IMAGE_PNG);
                break;
            default:
                headers.setContentType(MediaType.IMAGE_JPEG);
        }
        return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
    }
}
