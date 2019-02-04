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
import sunnn.sunsite.dto.request.ModifyIllustrator;
import sunnn.sunsite.dto.response.BaseResponse;
import sunnn.sunsite.dto.response.IllustratorInfoResponse;
import sunnn.sunsite.dto.response.IllustratorListResponse;
import sunnn.sunsite.dto.response.PictureListResponse;
import sunnn.sunsite.exception.IllegalFileRequestException;
import sunnn.sunsite.service.GalleryService;
import sunnn.sunsite.service.IllustratorService;
import sunnn.sunsite.util.StatusCode;

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

    @GetMapping(value = "/list")
    @ResponseBody
    public IllustratorListResponse illustratorList(@RequestParam("p") int page) {
        return illustratorService.getIllustratorList(page);
    }

    @GetMapping(value = "/info")
    @ResponseBody
    public IllustratorInfoResponse collectionInfo(@RequestParam("n") String illustrator) {
        return illustratorService.getIllustratorInfo(illustrator);
    }

    @GetMapping(value = "/detail")
    @ResponseBody
    public PictureListResponse illustratorDetail(@RequestParam("n") String illustrator, @RequestParam("p") int page) {
        return galleryService.getPictureListByiIllustrator(illustrator, page);
    }

    @GetMapping(value = "/download/{name}")
    @ResponseBody
    public ResponseEntity downloadIllustrator(@PathVariable("name") String name) throws IllegalFileRequestException, IOException {
        File file = illustratorService.download(name);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment",
                new String(file.getName().getBytes(), "ISO-8859-1"));
        return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
    }

    @PostMapping(value = "/modify")
    @ResponseBody
    public BaseResponse modifyGroup(@Valid @RequestBody ModifyIllustrator modifyInfo) {
        StatusCode modifyAlias = illustratorService.modifyAlias(
                modifyInfo.getIllustrator(), modifyInfo.getAliases());
        StatusCode modifyName = illustratorService.modifyName(modifyInfo.getIllustrator(), modifyInfo.getNewName());

        if (!modifyAlias.equals(StatusCode.OJBK))
            return new BaseResponse(modifyAlias);
        else if (!modifyName.equals(StatusCode.OJBK))
            return new BaseResponse(modifyName);
        else
            return new BaseResponse(StatusCode.OJBK);
    }


    @PostMapping(value = "/delete")
    @ResponseBody
    public BaseResponse deleteIllustrator(@Valid @RequestBody DeleteRequest info) {
        return new BaseResponse(
                illustratorService.delete(info.getDeleteInfo()));
    }
}