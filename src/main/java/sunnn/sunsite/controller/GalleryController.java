package sunnn.sunsite.controller;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sunnn.sunsite.dto.request.ModifyPicture;
import sunnn.sunsite.dto.request.UploadPictureInfo;
import sunnn.sunsite.dto.response.*;
import sunnn.sunsite.service.GalleryService;
import sunnn.sunsite.service.PictureService;
import sunnn.sunsite.util.StatusCode;

import javax.validation.Valid;

@Controller
@RequestMapping("/gallery")
public class GalleryController {

    private final GalleryService galleryService;

    private final PictureService pictureService;

    @Autowired
    public GalleryController(GalleryService galleryService, PictureService pictureService) {
        this.galleryService = galleryService;
        this.pictureService = pictureService;
    }

    @GetMapping(value = "/list")
    @ResponseBody
    public PictureListResponse pictureList(@RequestParam("p") int page,
                                           @RequestParam(value = "t", required = false) String type,
                                           @RequestParam(value = "o", required = false) String orientation) {
        if ((type == null || type.isEmpty()) && (orientation == null || orientation.isEmpty()))
            return galleryService.getPictureList(page);
        else
            return galleryService.getPictureList(type, orientation, page);
    }

    @GetMapping(value = "/listByIllustrator")
    @ResponseBody
    public PictureListResponse listByIllustrator(@RequestParam("n") String illustrator, @RequestParam("p") int page) {
        return galleryService.getPictureListByIllustrator(illustrator, page);
    }

    @GetMapping(value = "/listByCollection")
    @ResponseBody
    public PictureListResponse listByCollection(@RequestParam("seq") long sequence, @RequestParam("p") int page) {
        return galleryService.getPictureListByCollection(sequence, page);
    }

    @GetMapping(value = "/{sequence}")
    @ResponseBody
    public PictureInfoResponse pictureInfo(@PathVariable("sequence") long sequence) {
        return galleryService.getPictureInfo(sequence);
    }

    @PostMapping(value = "/upload")
    @ResponseBody
    public BaseResponse upload(@RequestParam("file") MultipartFile[] files, @RequestParam("illustrator") String[] illustrator,
                                     @RequestParam("group") String group, @RequestParam("collection") String collection) {
        if (files.length == 0 || group.trim().isEmpty() || collection.trim().isEmpty())
            return new BaseResponse(StatusCode.ILLEGAL_INPUT);

        UploadPictureInfo info = new UploadPictureInfo()
                .setIllustrator(illustrator)
                .setGroup(group)
                .setCollection(collection);
        String uploadCode = String.valueOf(System.currentTimeMillis())
                + SecurityUtils.getSubject().getSession().getId();

        return new BaseResponse(pictureService.uploadPicture(files, info, uploadCode));
    }

    @PostMapping(value = "/modify")
    @ResponseBody
    public BaseResponse modify(@Valid @RequestBody ModifyPicture modifyInfo) {
        long sequence = Long.valueOf(modifyInfo.getSequence());
        // 这个修改必须在前
        StatusCode modifyIllustrator = pictureService.modifyIllustrator(sequence, modifyInfo.getIllustrator());
        ModifyResultResponse response = pictureService.modifyPicture(sequence, modifyInfo.getName());

        if (response.getCode() == 0 && !modifyIllustrator.equals(StatusCode.OJBK))
            response.setCode(modifyIllustrator.getCode())
                    .setMsg(modifyIllustrator.name());
        return response;
    }

    @PostMapping(value = "/delete")
    @ResponseBody
    public BaseResponse deletePicture(@RequestParam("sequence") long sequence) {
        return new BaseResponse(pictureService.delete(sequence));
    }
}
