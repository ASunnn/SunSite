package sunnn.sunsite.controller;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sunnn.sunsite.dto.CollectionBase;
import sunnn.sunsite.dto.request.DeleteRequest;
import sunnn.sunsite.dto.response.BaseResponse;
import sunnn.sunsite.dto.response.CollectionListResponse;
import sunnn.sunsite.dto.response.CollectionInfoResponse;
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

    @RequestMapping(value = "/create")
    @ResponseBody
    public BaseResponse createCollection(@Valid @RequestBody CollectionBase info) {
        return new BaseResponse(collectionService.createCollection(info));
    }

    @RequestMapping(value = "/list")
    @ResponseBody
    public CollectionListResponse collectionList(@RequestParam("p") int page) {
        return collectionService.getCollectionList(page);
    }

    @RequestMapping(value = "/info")
    @ResponseBody
    public CollectionInfoResponse collectionDetail(@RequestParam("seq") long sequence, @RequestParam("p") int page) {
        return galleryService.getPictureListInCollection(sequence, page);
    }

    @RequestMapping(value = "/download/{sequence}")
    @ResponseBody
    public ResponseEntity downloadCollection(@PathVariable("sequence") long sequence) throws IllegalFileRequestException, IOException {
        File file = collectionService.download(sequence);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment",
                new String(file.getName().getBytes(), "ISO-8859-1"));
        return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    public BaseResponse deleteCollection(@Valid @RequestBody DeleteRequest info) {
        return new BaseResponse(
                collectionService.delete(Long.valueOf(info.getDeleteInfo())));
    }
}
