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
import sunnn.sunsite.dto.response.CollectionListResponse;
import sunnn.sunsite.dto.response.TypeListResponse;
import sunnn.sunsite.exception.IllegalFileRequestException;
import sunnn.sunsite.service.CollectionService;
import sunnn.sunsite.service.TypeService;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping("/type")
public class TypeController {

    private final TypeService typeService;

    private final CollectionService collectionService;

    @Autowired
    public TypeController(TypeService typeService, CollectionService collectionService) {
        this.typeService = typeService;
        this.collectionService = collectionService;
    }

    @GetMapping(value = "/list")
    @ResponseBody
    public TypeListResponse typeList() {
        return typeService.getTypeList();
    }

    @GetMapping(value = "/detail")
    @ResponseBody
    public CollectionListResponse typeDetail(@RequestParam("n") String type, @RequestParam("p") int page) {
        return collectionService.getCollectionListByType(type, page);
    }

    @GetMapping(value = "/download/{name}")
    @ResponseBody
    public ResponseEntity downloadType(@PathVariable("name") String name) throws IllegalFileRequestException, IOException {
        File file = typeService.download(name);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment",
                new String(file.getName().getBytes(), "ISO-8859-1"));
        return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
    }

    @PostMapping(value = "/delete")
    @ResponseBody
    public BaseResponse deleteType(@Valid @RequestBody DeleteRequest info) {
        return new BaseResponse(
                typeService.delete(info.getDeleteInfo()));
    }
}
