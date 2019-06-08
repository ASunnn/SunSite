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
import sunnn.sunsite.dto.response.*;
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
    public IllustratorListResponse illustratorList(@RequestParam("p") int page, @RequestParam(value = "query", required = false) String query) {
        if (query == null || query.isEmpty())
            return illustratorService.getIllustratorList(page);
        else
            return illustratorService.getIllustratorList(query.trim(), page);
    }

    @GetMapping(value = "/info")
    @ResponseBody
    public IllustratorInfoResponse illustratorInfo(@RequestParam("n") String illustrator) {
        return illustratorService.getIllustratorInfo(illustrator);
    }

    @GetMapping(value = "/detail")
    @ResponseBody
    public PictureListResponse illustratorDetail(@RequestParam("n") String illustrator, @RequestParam("p") int page) {
        return galleryService.getPictureListByiIllustrator(illustrator, page);
    }

    @GetMapping(value = "/m/{name}")
    @ResponseBody
    public ResponseEntity illustratorThumbnail(@PathVariable("name") String name) throws IOException {
        File file = illustratorService.getIllustratorThumbnail(name);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
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
    public ModifyResultResponse modifyGroup(@Valid @RequestBody ModifyIllustrator modifyInfo) {
        StatusCode modifyAlias = illustratorService.modifyAlias(
                modifyInfo.getIllustrator(), modifyInfo.getAliases());
        StatusCode modifyName = illustratorService.modifyName(modifyInfo.getIllustrator(), modifyInfo.getNewName());

        String newLink = modifyName.equals(StatusCode.OJBK) ? modifyInfo.getNewName() : modifyInfo.getIllustrator();
        ModifyResultResponse response;
        if (!modifyAlias.equals(StatusCode.OJBK))
            response = new ModifyResultResponse(modifyAlias);
        else if (!modifyName.equals(StatusCode.OJBK))
            response = new ModifyResultResponse(modifyName);
        else
            response = new ModifyResultResponse(StatusCode.OJBK);

        return response.setNewLinkInfo(newLink);
    }


    @PostMapping(value = "/delete")
    @ResponseBody
    public BaseResponse deleteIllustrator(@Valid @RequestBody DeleteRequest info) {
        return new BaseResponse(
                illustratorService.delete(info.getDeleteInfo()));
    }
}