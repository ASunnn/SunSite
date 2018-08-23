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
import sunnn.sunsite.dto.response.PictureListResponse;
import sunnn.sunsite.exception.IllegalFileRequestException;
import sunnn.sunsite.service.PoolService;
import sunnn.sunsite.util.StatusCode;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping("/pool")
public class PoolController {

    private final PoolService poolService;

    @Autowired
    public PoolController(PoolService poolService) {
        this.poolService = poolService;
    }

    @GetMapping(value = "/list")
    @ResponseBody
    public PictureListResponse pictureList(@RequestParam("illustrator") String illustrator,
                                           @RequestParam("collection") String collection,
                                           @RequestParam("p") int page,
                                           @RequestParam("s") int pageSize) {
        return poolService.getPictureFromPool(illustrator, collection, page, pageSize);
    }

    @GetMapping(value = "/download")
    public ResponseEntity downloadAll(@RequestParam("i") String illustrator, @RequestParam("c") String collection)
            throws IllegalFileRequestException, IOException {
        File file = poolService.download(illustrator, collection);
        if (file == null)
            throw new IOException();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment",
                new String(file.getName().getBytes(), "ISO-8859-1"));
        return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
    }

    @PostMapping(value = "/delete")
    @ResponseBody
    public BaseResponse deleteIllustrator(@Valid @RequestBody DeleteRequest deleteRequest) {
        String[] info = deleteRequest.getDeleteInfo().split("/");
        if (info.length != 2)
            return new BaseResponse(StatusCode.ILLEGAL_DATA);
        return new BaseResponse(
                poolService.delete(info[0], info[1]));
    }

}
