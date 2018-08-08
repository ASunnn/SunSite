package sunnn.sunsite.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import sunnn.sunsite.service.PictureInfoService;

@Controller
@RequestMapping("/illustrator")
public class IllustratorController {

    private final PictureInfoService illustratorService;

    @Autowired
    public IllustratorController(@Qualifier("illustratorServiceImpl") PictureInfoService illustratorService) {
        this.illustratorService = illustratorService;
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
