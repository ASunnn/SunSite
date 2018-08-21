package sunnn.sunsite.controller;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sunnn.sunsite.dto.request.ChangeName;
import sunnn.sunsite.dto.request.DeleteRequest;
import sunnn.sunsite.dto.response.BaseResponse;
import sunnn.sunsite.dto.response.PictureInfoInfoListResponse;
import sunnn.sunsite.exception.IllegalFileRequestException;
import sunnn.sunsite.service.PictureInfoService;
import sunnn.sunsite.util.StatusCode;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/collection")
public class CollectionController {

    private final PictureInfoService collectionService;

    private final PictureInfoService illustratorService;

    @Autowired
    public CollectionController(@Qualifier("collectionServiceImpl") PictureInfoService collectionService,
                                @Qualifier("illustratorServiceImpl") PictureInfoService illustratorService) {
        this.collectionService = collectionService;
        this.illustratorService = illustratorService;
    }

    @GetMapping(value = "illustrators")
    @ResponseBody
    public PictureInfoInfoListResponse getCollections(@RequestParam("c") String collectionName) {
        List<String> relatedList = collectionService.getRelatedList(collectionName);

        if (relatedList.isEmpty())
            return new PictureInfoInfoListResponse(StatusCode.NO_DATA);

        String[] illustratorList = new String[relatedList.size()];
        relatedList.toArray(illustratorList);

        String[] thumbnailSequenceList = new String[relatedList.size()];
        for (int i = 0; i < illustratorList.length; ++i)
            thumbnailSequenceList[i] =
                    String.valueOf(illustratorService.getThumbnailSequence(illustratorList[i]));

        return new PictureInfoInfoListResponse(
                StatusCode.OJBK, illustratorList, thumbnailSequenceList);
    }

    @GetMapping(value = "download")
    public ResponseEntity downloadAll(@RequestParam("n") String collectionName)
            throws IllegalFileRequestException, IOException {
        File file = collectionService.download(collectionName);
        if (file == null)
            throw new IOException();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", file.getName());
        return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
    }

    @PostMapping(value = "modify")
    public BaseResponse changeName(@RequestBody ChangeName nameInfo) {
        return new BaseResponse(
                collectionService.changeName(
                        nameInfo.getOldName(), nameInfo.getNewName()));
    }

    @PostMapping(value = "delete")
    @ResponseBody
    public BaseResponse deleteIllustrator(@RequestBody DeleteRequest deleteRequest) {
        return new BaseResponse(
                collectionService.delete(
                        deleteRequest.getDeleteInfo()));
    }

//    @GetMapping(value = "/list")
//    @ResponseBody
//    public GalleryInfoResponse getIllustratorList() {
//        @SuppressWarnings("unchecked")
//        List<Collection> collections = collectionService.getList();
//
//        if (collections.isEmpty())
//            return new GalleryInfoResponse(StatusCode.NO_DATA);
//
//        GalleryInfoResponse<Collection> response = new GalleryInfoResponse<>(StatusCode.OJBK);
//        return response.convertTo(collections);
//    }

}
