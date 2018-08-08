package sunnn.sunsite.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import sunnn.sunsite.service.PictureInfoService;

@Controller
@RequestMapping("/type")
public class TypeController {

    private final PictureInfoService typeService;

    @Autowired
    public TypeController(@Qualifier("typeServiceImpl") PictureInfoService typeService) {
        this.typeService = typeService;
    }

//    @GetMapping(value = "/list")
//    @ResponseBody
//    public GalleryInfoResponse getIllustratorList() {
//        @SuppressWarnings("unchecked")
//        List<Type> types = typeService.getList();
//
//        if (types.isEmpty())
//            return new GalleryInfoResponse(StatusCode.NO_DATA);
//
//        GalleryInfoResponse<Type> response = new GalleryInfoResponse<>(StatusCode.OJBK);
//        return response.convertTo(types);
//    }
}
