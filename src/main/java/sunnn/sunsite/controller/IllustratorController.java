package sunnn.sunsite.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sunnn.sunsite.dto.request.ChangeName;
import sunnn.sunsite.dto.request.DeleteRequest;
import sunnn.sunsite.dto.response.BaseResponse;
import sunnn.sunsite.service.PictureInfoService;
import sunnn.sunsite.util.StatusCode;

@Controller
@RequestMapping("/illustrator")
public class IllustratorController {

    private final PictureInfoService illustratorService;

    @Autowired
    public IllustratorController(@Qualifier("illustratorServiceImpl") PictureInfoService illustratorService) {
        this.illustratorService = illustratorService;
    }

    @GetMapping(value = "collections")
    @ResponseBody
    public void getCollections(@RequestParam("i") String illustratorName) {
        illustratorService.getRelatedList(illustratorName);
    }

    @GetMapping(value = "download")
    public ResponseEntity downloadAll(@RequestParam("n") String illustratorName) {
        return null;
    }

    @PostMapping(value = "modify")
    public BaseResponse changeName(@RequestBody ChangeName nameInfo) {
        return new BaseResponse(
                illustratorService.changeName(
                        nameInfo.getOldName(), nameInfo.getNewName()));
    }

    @PostMapping(value = "delete")
    public BaseResponse deleteIllustrator(@RequestBody DeleteRequest deleteRequest) {
        return new BaseResponse(
                illustratorService.delete(
                        deleteRequest.getDeleteInfo()));
    }

//    @GetMapping(value = "/list")
//    @ResponseBody
//    public GalleryInfoResponse getIllustratorList() {
//        @SuppressWarnings("unchecked")
//        List<Illustrator> illustrators = illustratorService.getList();
//
//        if (illustrators.isEmpty())
//            return new GalleryInfoResponse(StatusCode.NO_DATA);
//
//        GalleryInfoResponse<Illustrator> response = new GalleryInfoResponse<>(StatusCode.OJBK);
//        return response.convertTo(illustrators);
//    }

}
