package sunnn.sunsite.controller;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sunnn.sunsite.dto.request.ModifyIllustrator;
import sunnn.sunsite.dto.response.*;
import sunnn.sunsite.exception.IllegalFileRequestException;
import sunnn.sunsite.service.IllustratorService;
import sunnn.sunsite.util.StatusCode;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping("/illustrator")
public class IllustratorController {

    private final IllustratorService illustratorService;

    @Autowired
    public IllustratorController(IllustratorService illustratorService) {
        this.illustratorService = illustratorService;
    }

    @GetMapping(value = "/list")
    @ResponseBody
    public IllustratorListResponse illustratorList(@RequestParam("p") int page, @RequestParam(value = "query", required = false) String query) {
        if (query == null || query.isEmpty())
            return illustratorService.getIllustratorList(page);
        else
            return illustratorService.getIllustratorList(query.trim(), page);
    }

    @GetMapping(value = "/{name}")
    @ResponseBody
    public IllustratorInfoResponse illustratorInfo(@PathVariable("name") String name) {
        return illustratorService.getIllustratorInfo(name);
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

    @PostMapping(value = "/modify/{name}")
    @ResponseBody
    public ModifyResultResponse modifyGroup(@PathVariable("name") String name, @Valid @RequestBody ModifyIllustrator modifyInfo) {
        StatusCode modifyAlias = illustratorService.modifyAlias(name, modifyInfo.getAliases());
        StatusCode modifyName = illustratorService.modifyName(name, modifyInfo.getNewName());

        String newLink = modifyName.equals(StatusCode.OJBK) ? modifyInfo.getNewName() : name;
        ModifyResultResponse response;
        if (!modifyAlias.equals(StatusCode.OJBK))
            response = new ModifyResultResponse(modifyAlias);
        else if (!modifyName.equals(StatusCode.OJBK))
            response = new ModifyResultResponse(modifyName);
        else
            response = new ModifyResultResponse(StatusCode.OJBK);

        return response.setNewLink(newLink);
    }


    @PostMapping(value = "/delete/{name}")
    @ResponseBody
    public BaseResponse deleteIllustrator(@PathVariable("name") String name) {
        return new BaseResponse(illustratorService.delete(name));
    }
}