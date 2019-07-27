package sunnn.sunsite.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sunnn.sunsite.dao.*;
import sunnn.sunsite.dto.CollectionBase;
import sunnn.sunsite.dto.request.UploadPictureInfo;
import sunnn.sunsite.dto.response.ModifyResultResponse;
import sunnn.sunsite.entity.*;
import sunnn.sunsite.service.IllustratorService;
import sunnn.sunsite.service.PictureService;
import sunnn.sunsite.task.PictureIndexTask;
import sunnn.sunsite.task.ThumbnailTask;
import sunnn.sunsite.task.TimeUpdateTask;
import sunnn.sunsite.util.*;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PictureServiceImpl implements PictureService {

    private static Logger log = LoggerFactory.getLogger(PictureServiceImpl.class);

    private final IllustratorService illustratorService;

    @Resource
    private PicDao picDao;

    @Resource
    private PictureDao pictureDao;

    @Resource
    private IllustratorDao illustratorDao;

    @Resource
    private CollectionDao collectionDao;

    private final ThumbnailTask thumbnailTask;

    private final PictureIndexTask indexTask;

    private final TimeUpdateTask updateTask;

    private final FileCache fileCache;

    @Autowired
    public PictureServiceImpl(IllustratorService illustratorService, ThumbnailTask thumbnailTask, PictureIndexTask indexTask, TimeUpdateTask updateTask, FileCache fileCache) {
        this.illustratorService = illustratorService;
        this.thumbnailTask = thumbnailTask;
        this.indexTask = indexTask;
        this.updateTask = updateTask;
        this.fileCache = fileCache;
    }

    @Override
    public StatusCode uploadPicture(MultipartFile file, String uploadCode) {
        // 检测文件是否为空
        if (file == null || file.isEmpty())
            return StatusCode.ILLEGAL_INPUT;
        // 对文件名进行检查
        String fileName = file.getOriginalFilename().toLowerCase();
        if (!checkFileName(fileName))
            return StatusCode.ILLEGAL_INPUT;
        // 将图片放入缓存
        try {
            //将MultipartFile转换为File
            File f = FileUtils.storeFile(file,
                SunSiteProperties.tempPath + uploadCode + File.separator);
            fileCache.setFile(uploadCode, f);
        } catch (IOException e) {
            log.error("Trans TempFile Failed : " + e);
            //发生IO错误的情况下，直接返回
            return StatusCode.ERROR;
        }
        return StatusCode.OJBK;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,
            isolation = Isolation.DEFAULT)
    public StatusCode uploadInfoAndSave(UploadPictureInfo info) {
        // 获取上传的文件
        List<File> files = fileCache.getFile(info.getUploadCode());
        if (files == null)
            return StatusCode.UPLOAD_TIMEOUT;

        // 图片信息处理
        Collection c = collectionDao.findByInfo(info.getCollection().trim(), info.getGroup().trim());
        if (c == null)
            return StatusCode.ILLEGAL_INPUT;

        List<Illustrator> illustrators = illustratorHandler(info.getIllustrator());

        // 进行文件的保存
        for (File file : files) {
            // 生成图片系统信息
            Pic pictureData = generateInfo(file, c);
            if (pictureData == null)
                continue;
            // 存文件
            try {
                FileUtils.copyFile(file, SunSiteProperties.savePath + pictureData.getPath());
            } catch (IOException e) {
                log.error("Error When Save Pic " + pictureData.getName() + " : ", e);
                continue;
            }
            // 存数据库
            picDao.insert(pictureData);

            Picture pictureInfo = new Picture()
                    .setSequence(pictureData.getSequence())
                    .setName(pictureData.getName())
                    .setCollection(c.getCId())
                    .setIndex(Integer.MAX_VALUE);
            pictureDao.insert(pictureInfo);

            List<Artwork> artworks = new ArrayList<>();
            for (Illustrator i : illustrators) {
                artworks.add(new Artwork()
                        .setIllustrator(i.getId())
                        .setSequence(pictureData.getSequence()));
            }
            if (!artworks.isEmpty())
                illustratorDao.insertAllArtwork(artworks);

            // 设置缩略图任务
            thumbnailTask.submit(
                    SunSiteProperties.savePath + pictureData.getPath(), pictureData.getName(), pictureData.getThumbnailName());
        }
        // 设置index更新任务
        indexTask.submit(c.getCId(), pictureDao.countByCollection(c.getCId()));
        // 设置时间戳更新任务
        updateTask.submit(c.getCId());

        return StatusCode.OJBK;
    }

    private Pic generateInfo(File file, Collection c) {
        Pic picture = new Pic();
        CollectionBase info = collectionDao.findBaseInfo(c.getCId());

        String md5Source = info.getGroup() + info.getCollection() + file.getName();
        long sequence = MD5s.getMD5Sequence(md5Source);
        if (sequence == -1)
            return null;
        if (picDao.find(sequence) != null) {
            log.warn("Duplicate Pic : " + file.getName());
            log.warn("Duplicate Sequence : " + sequence);
            return null;
        }

        picture.setSequence(sequence)
                .setName(file.getName())
                .setSize(file.length())
                .setUploadTime(new Timestamp(System.currentTimeMillis()))
                .setPath(info.getType()
                        + File.separator
                            + info.getGroup()
                        + File.separator
                            + info.getCollection()
                        + File.separator);

        //缩略图文件名
        String thumbnailName = Pic.THUMBNAIL_PREFIX + picture.getName();
        Matcher extensionNameMatcher = Pattern.compile("\\.(jpg|jpeg)$")
                .matcher(picture.getName());
        if (!extensionNameMatcher.find())
            thumbnailName = thumbnailName.substring(
                    0, thumbnailName.lastIndexOf('.')) + ".jpg";
        picture.setThumbnailName(thumbnailName);

        // get图片长宽
        try {
            int[] pictureSize = Utils.getPictureSize(file.getPath());
            picture.setWidth(pictureSize[0]);
            picture.setHeight(pictureSize[1]);
        } catch (IOException e) {
            log.error("Cannot Get UploadPictureInfo Info : ", e);
            return null;
        }

        if (picture.getWidth() < picture.getHeight())
            picture.setVOrH(-1);
        else if (picture.getWidth() > picture.getHeight())
            picture.setVOrH(1);
        else
            picture.setVOrH(0);

        return picture;
    }

    private List<Illustrator> illustratorHandler(String illustrator) {
        String[] illustratorInfo;
        if (illustrator != null && !illustrator.isEmpty())
            illustratorInfo = illustrator.split(SunsiteConstant.SEPARATOR);
        else
            illustratorInfo = new String[]{Illustrator.DEFAULT_VALUE};

        List<Illustrator> illustrators = new ArrayList<>();
        for (String i : illustratorInfo) {
            String iTrim = i.trim();
            if (!iTrim.isEmpty())
                illustrators.add(illustratorService.createIllustrator(iTrim));
        }

        return illustrators;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW,
            isolation = Isolation.DEFAULT)
    public ModifyResultResponse modifyPicture(long sequence, String newName) {
        // 合法性检查
        Picture picture = pictureDao.find(sequence);
        if (picture == null || !checkFileName(newName))
            return new ModifyResultResponse(StatusCode.ILLEGAL_INPUT, sequence);
        if (newName.equals(picture.getName()))
            return new ModifyResultResponse(StatusCode.OJBK, sequence);

        Pic pic = picDao.find(picture.getSequence());
        CollectionBase baseInfo = collectionDao.findBaseInfo(picture.getCollection());

        // 查重
        String md5Source = baseInfo.getGroup() + baseInfo.getCollection() + newName;
        long newSequence = MD5s.getMD5Sequence(md5Source);

        if (picDao.find(newSequence) != null)
            return new ModifyResultResponse(StatusCode.MODIFY_FAILED, sequence);

        String thumbnailName = Pic.THUMBNAIL_PREFIX + newName;
        // 文件操作
        String path = SunSiteProperties.savePath + pic.getPath() + pic.getName();
        if (!FileUtils.rename(new File(path), newName))
            return new ModifyResultResponse(StatusCode.MODIFY_FAILED, sequence);
        String thumbPath = SunSiteProperties.savePath + pic.getPath() + pic.getThumbnailName();
        if (!FileUtils.rename(new File(thumbPath), thumbnailName))
            return new ModifyResultResponse(StatusCode.MODIFY_FAILED, sequence);

        // 更新数据库和index
        pictureDao.updateName(pic.getSequence(), newSequence, newName);
        picDao.updateName(pic.getSequence(), newSequence, newName, thumbnailName);
        illustratorDao.updatePicture(pic.getSequence(), newSequence);

        indexTask.submit(picture.getCollection(), pictureDao.countByCollection(picture.getCollection()));

        return new ModifyResultResponse(StatusCode.OJBK, newSequence);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW,
            isolation = Isolation.DEFAULT)
    public StatusCode modifyIllustrator(long sequence, String illustrators) {
        Picture picture = pictureDao.find(sequence);
        if (picture == null)
            return StatusCode.ILLEGAL_INPUT;

        List<Illustrator> newIllustratorList = illustratorHandler(illustrators);
        illustratorDao.deletePicture(sequence);

        List<Artwork> artworks = new ArrayList<>();
        for (Illustrator i : newIllustratorList) {
            artworks.add(new Artwork()
                    .setIllustrator(i.getId())
                    .setSequence(sequence));
        }
        illustratorDao.insertAllArtwork(artworks);

        return StatusCode.OJBK;
    }

    @Override
    @Transactional
    public StatusCode delete(long sequence) {
        Pic p = picDao.find(sequence);
        if (p == null)
            return StatusCode.DELETE_FAILED;

        pictureDao.delete(sequence);
        picDao.delete(sequence);
        illustratorDao.deletePicture(sequence);

        //删除文件本体
        String path = SunSiteProperties.savePath + p.getPath();
        if (!FileUtils.deleteFile(path + p.getName())
                | !FileUtils.deleteFile(path + p.getThumbnailName()))
            log.warn("Delete Picture Failed : " + path + p.getName());

        return StatusCode.OJBK;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,
            isolation = Isolation.DEFAULT)
    public StatusCode deleteCollection(long sequence) {
        List<Picture> pictures = pictureDao.findAllByCollection(sequence);

        for (Picture p : pictures) {
            picDao.delete(p.getSequence());
            illustratorDao.deletePicture(p.getSequence());
        }
        pictureDao.deleteAllByCollection(sequence);

        return StatusCode.OJBK;
    }

    /**
     * 对文件名进行检查
     *
     * @return  true：合法
     *          false：非法
     */
    private boolean checkFileName(String fileName) {
        Matcher extensionNameMatcher = Pattern.compile("\\.(jpg|jpeg|png)$")
                .matcher(fileName);
        return extensionNameMatcher.find() && !FileUtils.fileNameMatcher(fileName);
    }
}
