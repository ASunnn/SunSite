package sunnn.sunsite.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sunnn.sunsite.dao.PictureDao;
import sunnn.sunsite.dto.PictureBase;
import sunnn.sunsite.dto.response.PictureListResponse;
import sunnn.sunsite.entity.Pic;
import sunnn.sunsite.service.SearchByImageService;
import sunnn.sunsite.util.StatusCode;
import sunnn.sunsite.util.SunsiteConstant;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TempController {

    private final SearchByImageService searchByImageService;

    @Resource
    private PictureDao pictureDao;

    @Autowired
    public TempController(SearchByImageService searchByImageService) {
        this.searchByImageService = searchByImageService;
    }

    @RequestMapping(value = "/temp")
    public String temp() {
        return "temp";
    }

    @GetMapping(value = "/temp/searchByPicture")
    @ResponseBody
    public PictureListResponse pictureList() {
//        searchByImageService.initPictureHashData();
        return handler(searchByImageService.matcherPicture(new File("C:\\Users\\劉日豐\\Desktop\\__hazuki_watora_original_drawn_by_peko__86ce0e0ce073041b3846746882cb1a70.jpg")));
//        return new PictureListResponse(StatusCode.NO_DATA);
    }

    private PictureListResponse handler(long[] sequences) {
        List<PictureBase> pictureList = new ArrayList<>();
        for (long s : sequences) {
            PictureBase r = pictureDao.findBaseInfo(s);
            pictureList.add(r);
        }
        if (pictureList.isEmpty())
            return new PictureListResponse(StatusCode.NO_DATA);

        return new PictureListResponse(StatusCode.OJBK).convertTo(pictureList);
    }
}
