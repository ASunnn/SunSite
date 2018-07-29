package sunnn.sunsite.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sunnn.sunsite.dto.response.GalleryInfoResponse;
import sunnn.sunsite.entity.Illustrator;
import sunnn.sunsite.service.PictureInfoService;
import sunnn.sunsite.util.StatusCode;

import java.util.List;

@Controller
@RequestMapping("/illustrator")
public class IllustratorController {

    private final PictureInfoService illustratorService;

    @Autowired
    public IllustratorController(@Qualifier("illustratorServiceImpl") PictureInfoService illustratorService) {
        this.illustratorService = illustratorService;
    }

    @GetMapping(value = "/list")
    @ResponseBody
    public GalleryInfoResponse getIllustratorList() {
        @SuppressWarnings("unchecked")
        List<Illustrator> illustrators = illustratorService.getList();

        if (illustrators.isEmpty())
            return new GalleryInfoResponse(StatusCode.NO_DATA);

        GalleryInfoResponse<Illustrator> response = new GalleryInfoResponse<>(StatusCode.OJBK);
        return response.convertTo(illustrators);
    }

}
