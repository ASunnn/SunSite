package sunnn.sunsite.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sunnn.sunsite.dao.*;
import sunnn.sunsite.dto.PicInfo;
import sunnn.sunsite.dto.response.MsgResponse;
import sunnn.sunsite.dto.response.PictureInfoResponse;
import sunnn.sunsite.dto.response.PictureListResponse;
import sunnn.sunsite.entity.Collection;
import sunnn.sunsite.entity.Pic;
import sunnn.sunsite.entity.Illustrator;
import sunnn.sunsite.entity.Picture;
import sunnn.sunsite.exception.IllegalFileRequestException;
import sunnn.sunsite.util.SunsiteConstant;
import sunnn.sunsite.util.StatusCode;
import sunnn.sunsite.util.SunSiteProperties;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static sunnn.sunsite.util.Utils.isIllegalPageParam;

@Service
public class GalleryService {

    private static Logger log = LoggerFactory.getLogger(GalleryService.class);

    @Resource
    private PicDao picDao;

    @Resource
    private PictureDao pictureDao;

    @Resource
    private CollectionDao collectionDao;

    @Resource
    private IllustratorDao illustratorDao;

    private final MessageBoxService messageBoxService;

    @Autowired
    public GalleryService(MessageBoxService messageBoxService) {
        this.messageBoxService = messageBoxService;
    }

    public PictureListResponse getPictureList(int page) {
        if (isIllegalPageParam(page))
            return new PictureListResponse(StatusCode.ILLEGAL_INPUT);

        int size = SunsiteConstant.PAGE_SIZE;
        int skip = page * size;
        List<Pic> pictures = picDao.findAll(skip, size);

        if (pictures.isEmpty())
            return new PictureListResponse(StatusCode.NO_DATA);

        List<PicInfo> pictureList = new ArrayList<>();
        for (Pic p : pictures) {
            PicInfo r = pictureDao.findInfo(p.getSequence());
            pictureList.add(r);
        }

        int count = picDao.count();
        int pageCount = (int) Math.ceil((double) count / SunsiteConstant.PAGE_SIZE);

        return new PictureListResponse(StatusCode.OJBK)
                .setPageCount(pageCount)
                .convertTo(pictureList);
    }

    public PictureListResponse getPictureList(String type, String orientation, int page) {

        if (isIllegalPageParam(page))
            return new PictureListResponse(StatusCode.ILLEGAL_INPUT);

        int size = SunsiteConstant.PAGE_SIZE;
        int skip = page * size;

        int ornt = getCurrentOrientation(orientation);
        List<PicInfo> pictures = pictureDao.findAllInfoByFilter(type, ornt, skip, size);

        if (pictures.isEmpty())
            return new PictureListResponse(StatusCode.NO_DATA);

        int count = pictureDao.countByFilter(type, ornt);
        int pageCount = (int) Math.ceil((double) count / SunsiteConstant.PAGE_SIZE);

        return new PictureListResponse(StatusCode.OJBK)
                .setPageCount(pageCount)
                .convertTo(pictures);
    }

    public PictureListResponse getPictureListByCollection(long cId, int page) {
        Collection c = collectionDao.find(cId);
        if (c == null || isIllegalPageParam(page))
            return new PictureListResponse(StatusCode.ILLEGAL_INPUT);

        int size = SunsiteConstant.PAGE_SIZE;
        int skip = page * size;

        List<PicInfo> pictureList = pictureDao.findAllInfoByCollection(cId, skip, size);
        if (pictureList.isEmpty())
            return new PictureListResponse(StatusCode.NO_DATA);

        int count = pictureDao.countByCollection(cId);
        int pageCount = (int) Math.ceil((double) count / SunsiteConstant.PAGE_SIZE);

        return new PictureListResponse(StatusCode.OJBK)
                .setPageCount(pageCount)
                .convertTo(pictureList);
    }

    public PictureListResponse getPictureListByIllustrator(String illustrator, int page) {
        if (isIllegalPageParam(page) || illustratorDao.find(illustrator) == null)
            return new PictureListResponse(StatusCode.ILLEGAL_INPUT);

        int size = SunsiteConstant.PAGE_SIZE;
        int skip = page * size;
        List<Pic> pictures = illustratorDao.findAllByIllustrator(illustrator, skip, size);

        if (pictures.isEmpty())
            return new PictureListResponse(StatusCode.NO_DATA);

        List<PicInfo> pictureList = new ArrayList<>();
        for (Pic p : pictures) {
            PicInfo r = pictureDao.findInfo(p.getSequence());
            pictureList.add(r);
        }

        long count = illustratorDao.countByIllustrator(illustrator);
        int pageCount = (int) Math.ceil((double) count / SunsiteConstant.PAGE_SIZE);

        return new PictureListResponse(StatusCode.OJBK)
                .setPageCount(pageCount)
                .convertTo(pictureList);
    }

    public File getThumbnail(long sequence) {
        Pic picture = picDao.find(sequence);
        if (picture != null) {
            String path = SunSiteProperties.savePath + picture.getPath() + picture.getThumbnailName();
            File f = new File(path);
            if (f.exists())
                return f;
        } else
            log.warn("Illegal File Request : " + sequence);
//        org.springframework.core.io.Resource resource = new ClassPathResource("/static/404.jpg");
        File f = new File(SunSiteProperties.missPicture);
        if (f.exists())
            return f;
        log.warn("404.jpg Miss");
        return null;
    }

    public File getPictureFile(long sequence) throws IllegalFileRequestException {
        Pic picture = picDao.find(sequence);
        if (picture == null)
            throw new IllegalFileRequestException(String.valueOf(sequence));

        String path = SunSiteProperties.savePath + picture.getPath() + picture.getName();
        return new File(path);
    }

    public PictureInfoResponse getPictureInfo(long sequence) {
        Pic pictureData = picDao.find(sequence);
        if (pictureData == null)
            return new PictureInfoResponse(StatusCode.ILLEGAL_INPUT);

        PictureInfoResponse response = new PictureInfoResponse(StatusCode.OJBK);
        response.setSequence(pictureData.getSequence())
                .setName(pictureData.getName())
                .setSize(pictureData.getSize())
                .setWidth(pictureData.getWidth())
                .setHeight(pictureData.getHeight());

        PicInfo info = pictureDao.findInfo(sequence);
        response.setGroup(info.getGroup())
                .setCId(String.valueOf(info.getCId()))
                .setCollection(info.getCollection());

        Picture p = pictureDao.find(sequence);
        long[] seq = getClosePicture(p);
        response.setPrev(seq[0])
                .setNext(seq[1]);

        List<Illustrator> illustrators = illustratorDao.findAllByPicture(sequence);
        String[] is = new String[illustrators.size()];
        for (int i = 0; i < illustrators.size(); ++i) {
            is[i] = illustrators.get(i).getName();
        }
        response.setIllustrator(is);

        return response;
    }

    public MsgResponse checkMsgBox() {
        String msg = messageBoxService.getMessage();

        if (msg == null)
            return new MsgResponse(StatusCode.NO_DATA);

        return new MsgResponse(StatusCode.OJBK).setMsg(msg);
    }

    private long[] getClosePicture(Picture p) {
        List<Picture> pictures = pictureDao.findAllByCollection(p.getCollection());

        int index = pictures.indexOf(p);

        long[] s = new long[2];
        s[0] = index == 0 ? -1 : pictures.get(index - 1).getSequence();
        s[1] = index + 1 == pictures.size() ? -1 : pictures.get(index + 1).getSequence();
        return s;
    }

    private int getCurrentOrientation(String orientation) {
        if (orientation.equals("Landscape"))
            return 1;
        else if (orientation.equals("Portrait"))
            return -1;
        return 0;
    }
}
