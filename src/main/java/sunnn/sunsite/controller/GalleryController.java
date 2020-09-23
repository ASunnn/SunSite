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

//    TODO 考虑合并上传
    @PostMapping(value = "/upload")
    @ResponseBody
    public FileUploadResponse upload(@RequestParam("file") MultipartFile[] files) {
        // notice:RequestParam里的值需要与页面内的id一致
        if (files.length == 0)
            return new FileUploadResponse(StatusCode.ILLEGAL_INPUT);

        String uploadCode = String.valueOf(System.currentTimeMillis())
                + SecurityUtils.getSubject().getSession().getId();

        for (MultipartFile file : files) {
            StatusCode code = pictureService.uploadPicture(file, uploadCode);
            // 非法文件，直接返回错误
            if (code != StatusCode.OJBK)
                return new FileUploadResponse(code);
        }

        return new FileUploadResponse(StatusCode.OJBK, uploadCode);
    }

    @PostMapping(value = "/uploadInfo")
    @ResponseBody
    public BaseResponse upload(@Valid @RequestBody UploadPictureInfo info) {
        return new BaseResponse(pictureService.uploadInfoAndSave(info));
    }

    @PostMapping(value = "/modify/{sequence}")
    @ResponseBody
    public BaseResponse modify(@PathVariable("sequence") long sequence, @Valid @RequestBody ModifyPicture modifyInfo) {
        // 这个修改必须在前
        StatusCode modifyIllustrator = pictureService.modifyIllustrator(sequence, modifyInfo.getIllustrators());
        ModifyResultResponse modifyPicture = pictureService.modifyPicture(sequence, modifyInfo.getName());

        if (modifyPicture.getCode() == 0 && !modifyIllustrator.equals(StatusCode.OJBK))
            modifyPicture.setCode(modifyIllustrator.getCode())
                    .setMsg(modifyIllustrator.name());
        return modifyPicture;
    }

    @PostMapping(value = "/delete/{sequence}")
    @ResponseBody
    public BaseResponse deletePicture(@PathVariable("sequence") long sequence) {
        return new BaseResponse(pictureService.delete(sequence));
    }
}
