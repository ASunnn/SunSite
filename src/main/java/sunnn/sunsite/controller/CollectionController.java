package sunnn.sunsite.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import sunnn.sunsite.service.PictureInfoService;

@Controller
@RequestMapping("/collection")
public class CollectionController {

    private final PictureInfoService collectionService;

    @Autowired
    public CollectionController(@Qualifier("collectionServiceImpl") PictureInfoService collectionService) {
        this.collectionService = collectionService;
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
