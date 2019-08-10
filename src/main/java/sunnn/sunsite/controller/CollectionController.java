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
import sunnn.sunsite.dto.request.ModifyCollection;
import sunnn.sunsite.dto.request.UploadCollectionInfo;
import sunnn.sunsite.dto.response.*;
import sunnn.sunsite.exception.IllegalFileRequestException;
import sunnn.sunsite.service.CollectionService;
import sunnn.sunsite.service.GalleryService;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping("/collection")
public class CollectionController {

    private final CollectionService collectionService;

    private final GalleryService galleryService;

    @Autowired
    public CollectionController(CollectionService collectionService, GalleryService galleryService) {
        this.collectionService = collectionService;
        this.galleryService = galleryService;
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

    @GetMapping(value = "/info")
    @ResponseBody
    public CollectionInfoResponse collectionInfo(@RequestParam("seq") long sequence) {
        return collectionService.getCollectionInfo(sequence);
    }

    @GetMapping(value = "/detail")
    @ResponseBody
    public PictureListResponse collectionDetail(@RequestParam("seq") long sequence, @RequestParam("p") int page) {
        return galleryService.getPictureListInCollection(sequence, page);
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

    @PostMapping(value = "/modify")
    @ResponseBody
    public ModifyResultResponse modifyCollection(@Valid @RequestBody ModifyCollection modifyInfo) {
        return collectionService.modifyName(Long.valueOf(modifyInfo.getSequence()), modifyInfo.getNewName());
    }

    @PostMapping(value = "/delete")
    @ResponseBody
    public BaseResponse deleteCollection(@Valid @RequestBody DeleteRequest info) {
        return new BaseResponse(
                collectionService.delete(Long.valueOf(info.getDeleteInfo())));
    }
}
