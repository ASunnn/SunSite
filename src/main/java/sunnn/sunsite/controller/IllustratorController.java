package sunnn.sunsite.controller;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sunnn.sunsite.dto.request.DeleteRequest;
import sunnn.sunsite.dto.response.BaseResponse;
import sunnn.sunsite.dto.response.IllustratorListResponse;
import sunnn.sunsite.dto.response.PictureListResponse;
import sunnn.sunsite.exception.IllegalFileRequestException;
import sunnn.sunsite.service.GalleryService;
import sunnn.sunsite.service.IllustratorService;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping("/illustrator")
public class IllustratorController {

    private final IllustratorService illustratorService;

    private final GalleryService galleryService;

    @Autowired
    public IllustratorController(IllustratorService illustratorService, GalleryService galleryService) {
        this.illustratorService = illustratorService;
        this.galleryService = galleryService;
    }

    @RequestMapping(value = "/list")
    @ResponseBody
    public IllustratorListResponse illustratorList(@RequestParam("p") int page) {
        return illustratorService.getIllustratorList(page);
    }

    @RequestMapping(value = "/info")
    @ResponseBody
    public PictureListResponse illustratorDetail(@RequestParam("i") String illustrator, @RequestParam("p") int page) {
        return galleryService.getPictureListByiIllustrator(illustrator, page);
    }

    @RequestMapping(value = "/download/{name}")
    @ResponseBody
    public ResponseEntity downloadIllustrator(@PathVariable("name") String name) throws IllegalFileRequestException, IOException {
        File file = illustratorService.download(name);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment",
                new String(file.getName().getBytes(), "ISO-8859-1"));
        return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
    }


    @RequestMapping(value = "/delete")
    @ResponseBody
    public BaseResponse deleteIllustrator(@Valid @RequestBody DeleteRequest info) {
        return new BaseResponse(
                illustratorService.delete(info.getDeleteInfo()));
    }
}