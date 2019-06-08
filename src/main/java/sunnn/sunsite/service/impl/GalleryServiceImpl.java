package sunnn.sunsite.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sunnn.sunsite.dao.*;
import sunnn.sunsite.dto.PictureBase;
import sunnn.sunsite.dto.response.MsgResponse;
import sunnn.sunsite.dto.response.PictureInfoResponse;
import sunnn.sunsite.dto.response.PictureListResponse;
import sunnn.sunsite.entity.Collection;
import sunnn.sunsite.entity.Pic;
import sunnn.sunsite.entity.Illustrator;
import sunnn.sunsite.entity.Picture;
import sunnn.sunsite.exception.IllegalFileRequestException;
import sunnn.sunsite.service.GalleryService;
import sunnn.sunsite.service.MessageBoxService;
import sunnn.sunsite.util.SunsiteConstant;
import sunnn.sunsite.util.StatusCode;
import sunnn.sunsite.util.SunSiteProperties;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static sunnn.sunsite.util.Utils.isIllegalPageParam;

@Service
public class GalleryServiceImpl implements GalleryService {

    private static Logger log = LoggerFactory.getLogger(GalleryServiceImpl.class);

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
    public GalleryServiceImpl(MessageBoxService messageBoxService) {
        this.messageBoxService = messageBoxService;
    }

    @Override
    public PictureListResponse getPictureList(int page) {
        if (isIllegalPageParam(page))
            return new PictureListResponse(StatusCode.ILLEGAL_INPUT);

        int size = SunsiteConstant.PAGE_SIZE;
        int skip = page * size;
        List<Pic> pictures = picDao.findAll(skip, size);

        if (pictures.isEmpty())
            return new PictureListResponse(StatusCode.NO_DATA);

        List<PictureBase> pictureList = new ArrayList<>();
        for (Pic p : pictures) {
            PictureBase r = pictureDao.findBaseInfo(p.getSequence());
            pictureList.add(r);
        }

        int count = picDao.count();
        int pageCount = (int) Math.ceil((double) count / SunsiteConstant.PAGE_SIZE);

        return new PictureListResponse(StatusCode.OJBK)
                .setPageCount(pageCount)
                .convertTo(pictureList);
    }

    @Override
    public PictureListResponse getPictureListInCollection(long cId, int page) {
        Collection c = collectionDao.find(cId);
        if (c == null || isIllegalPageParam(page))
            return new PictureListResponse(StatusCode.ILLEGAL_INPUT);

        int size = SunsiteConstant.PAGE_SIZE;
        int skip = page * size;

        List<PictureBase> pictureList = pictureDao.findAllBaseInfoByCollection(cId, skip, size);
        if (pictureList.isEmpty())
            return new PictureListResponse(StatusCode.NO_DATA);

        int count = pictureDao.countByCollection(cId);
        int pageCount = (int) Math.ceil((double) count / SunsiteConstant.PAGE_SIZE);

        return new PictureListResponse(StatusCode.OJBK)
                .setPageCount(pageCount)
                .convertTo(pictureList);
    }

    @Override
    public PictureListResponse getPictureListByiIllustrator(String illustrator, int page) {
        if (isIllegalPageParam(page) || illustratorDao.find(illustrator) == null)
            return new PictureListResponse(StatusCode.ILLEGAL_INPUT);

        int size = SunsiteConstant.PAGE_SIZE;
        int skip = page * size;
        List<Pic> pictures = illustratorDao.findAllByIllustrator(illustrator, skip, size);

        if (pictures.isEmpty())
            return new PictureListResponse(StatusCode.NO_DATA);

        List<PictureBase> pictureList = new ArrayList<>();
        for (Pic p : pictures) {
            PictureBase r = pictureDao.findBaseInfo(p.getSequence());
            pictureList.add(r);
        }

        long count = illustratorDao.countByIllustrator(illustrator);
        int pageCount = (int) Math.ceil((double) count / SunsiteConstant.PAGE_SIZE);

        return new PictureListResponse(StatusCode.OJBK)
                .setPageCount(pageCount)
                .convertTo(pictureList);
    }

    @Override
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

    @Override
    public File getPictureFile(long sequence) throws IllegalFileRequestException {
        Pic picture = picDao.find(sequence);
        if (picture == null)
            throw new IllegalFileRequestException(String.valueOf(sequence));

        String path = SunSiteProperties.savePath + picture.getPath() + picture.getName();
        return new File(path);
    }

    @Override
    public PictureInfoResponse getPictureInfo(long sequence) {
        Pic pictureData = picDao.find(sequence);
        if (pictureData == null)
            return new PictureInfoResponse(StatusCode.ILLEGAL_INPUT);

        PictureInfoResponse response = new PictureInfoResponse(StatusCode.OJBK);
        response.setSequence(pictureData.getSequence())
                .setName(pictureData.getName())
                .setWidth(pictureData.getWidth())
                .setHeight(pictureData.getHeight());

        PictureBase baseInfo = pictureDao.findBaseInfo(sequence);
        response.setGroup(baseInfo.getGroup())
                .setCId(String.valueOf(baseInfo.getCId()))
                .setCollection(baseInfo.getCollection());

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

    private long[] getClosePicture(Picture p) {
        List<Picture> pictures = pictureDao.findAllByCollection(p.getCollection());

        int index = pictures.indexOf(p);

        long[] s = new long[2];
        s[0] = index == 0 ? -1 : pictures.get(index - 1).getSequence();
        s[1] = index + 1 == pictures.size() ? -1 : pictures.get(index + 1).getSequence();
        return s;
    }

    @Override
    public MsgResponse checkMsgBox() {
        String msg = messageBoxService.getMessage();

        if (msg == null)
            return new MsgResponse(StatusCode.NO_DATA);

        return new MsgResponse(StatusCode.OJBK).setMsg(msg);
    }
}
