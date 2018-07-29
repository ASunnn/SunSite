package sunnn.sunsite.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sunnn.sunsite.dto.response.GalleryInfoResponse;
import sunnn.sunsite.entity.Collection;
import sunnn.sunsite.service.PictureInfoService;
import sunnn.sunsite.util.StatusCode;

import java.util.List;

@Controller
@RequestMapping("/collection")
public class CollectionController {

    private final PictureInfoService collectionService;

    @Autowired
    public CollectionController(@Qualifier("collectionServiceImpl") PictureInfoService collectionService) {
        this.collectionService = collectionService;
    }

    @GetMapping(value = "/list")
    @ResponseBody
    public GalleryInfoResponse getIllustratorList() {
        @SuppressWarnings("unchecked")
        List<Collection> collections = collectionService.getList();

        if (collections.isEmpty())
            return new GalleryInfoResponse(StatusCode.NO_DATA);

        GalleryInfoResponse<Collection> response = new GalleryInfoResponse<>(StatusCode.OJBK);
        return response.convertTo(collections);
    }

}
