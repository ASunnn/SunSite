package sunnn.sunsite.controller;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sunnn.sunsite.dto.request.ModifyCollection;
import sunnn.sunsite.dto.request.UploadCollectionInfo;
import sunnn.sunsite.dto.response.*;
import sunnn.sunsite.exception.IllegalFileRequestException;
import sunnn.sunsite.service.CollectionService;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping("/collection")
public class CollectionController {

    private final CollectionService collectionService;

    @Autowired
    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @PostMapping(value = "/create")
    @ResponseBody
    public BaseResponse createCollection(@Valid @RequestBody UploadCollectionInfo info) {
        return new BaseResponse(collectionService.createCollection(info));
    }

    @GetMapping(value = "/list")
    @ResponseBody
    public CollectionListResponse collectionList(@RequestParam("p") int page) {
        return collectionService.getCollectionList(page);
    }

    @GetMapping(value = "/{sequence}")
    @ResponseBody
    public CollectionInfoResponse collectionInfo(@PathVariable("sequence") long sequence) {
        return collectionService.getCollectionInfo(sequence);
    }

    @GetMapping(value = "/m/{sequence}")
    @ResponseBody
    public ResponseEntity collectionThumbnail(@PathVariable("sequence") long sequence) throws IOException {
        File file = collectionService.getCollectionThumbnail(sequence);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
    }

    @GetMapping(value = "/download/{sequence}")
    @ResponseBody
    public ResponseEntity downloadCollection(@PathVariable("sequence") long sequence) throws IllegalFileRequestException, IOException {
        File file = collectionService.download(sequence);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment",
                new String(file.getName().getBytes(), "ISO-8859-1"));
        return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
    }

    @PostMapping(value = "/modify/{sequence}")
    @ResponseBody
    public ModifyResultResponse modifyCollection(@PathVariable("sequence") long sequence, @Valid @RequestBody ModifyCollection modifyInfo) {
        return collectionService.modifyName(sequence, modifyInfo.getNewName());
    }

    @PostMapping(value = "/delete/{sequence}")
    @ResponseBody
    public BaseResponse deleteCollection(@PathVariable("sequence") long sequence) {
        return new BaseResponse(collectionService.delete(sequence));
    }

    @GetMapping(value = "/listByGroup")
    @ResponseBody
    public CollectionListResponse listByGroup(@RequestParam("n") String group) {
        return collectionService.getCollectionListByGroup(group);
    }

    @GetMapping(value = "/listByType")
    @ResponseBody
    public CollectionListResponse listByType(@RequestParam("n") String type, @RequestParam("p") int page) {
        return collectionService.getCollectionListByType(type, page);
    }

}
