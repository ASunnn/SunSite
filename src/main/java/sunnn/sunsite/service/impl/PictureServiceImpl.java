package sunnn.sunsite.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sunnn.sunsite.dao.*;
import sunnn.sunsite.dto.CollectionBase;
import sunnn.sunsite.dto.request.UploadPictureInfo;
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
    private PicMapper picMapper;

    @Resource
    private PictureMapper pictureMapper;

    @Resource
    private IllustratorMapper illustratorMapper;

    @Resource
    private CollectionMapper collectionMapper;

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
        /*
            检测文件是否为空
         */
        if (file == null || file.isEmpty())
            return StatusCode.ILLEGAL_INPUT;
        /*
            对文件名进行检查
         */
        Matcher fileNameMatcher = Pattern.compile("\\.(jpg|jpeg|png|bmp)$")
                .matcher(file.getOriginalFilename());
        if (!fileNameMatcher.find())
            return StatusCode.ILLEGAL_INPUT;
        /*
            将图片放入缓存
         */
        try {
            //将MultipartFile转换为File
            File f = FileUtils.storeFile(file,
                SunSiteConstant.tempPath + uploadCode + SunSiteConstant.pathSeparator);
            fileCache.setFile(uploadCode, f);
        } catch (IOException e) {
            log.error("Trans TempFile Failed : " + e);
            //发生IO错误的情况下，直接返回
            return StatusCode.ERROR;
        }
        return StatusCode.OJBK;
    }

    @Override
    public StatusCode uploadInfoAndSave(UploadPictureInfo info) {
        /*
            获取上传的文件
         */
        List<File> files = fileCache.getFile(info.getUploadCode());
        if (files == null)
            return StatusCode.UPLOAD_TIMEOUT;
        /*
            图片信息处理
         */
        Collection c = collectionMapper.findByInfo(info.getCollection(), info.getGroup());
        if (c == null)
            return StatusCode.ILLEGAL_INPUT;

        String[] illustratorInfo;
        if (info.getIllustrator() != null && !info.getIllustrator().isEmpty())
            illustratorInfo = info.getIllustrator().split("，");
        else
            illustratorInfo = new String[]{Illustrator.DEFAULT_VALUE};

        List<Illustrator> illustrators = new ArrayList<>();
        for (String i : illustratorInfo) {
            illustrators.add(illustratorService.createIllustrator(i.trim()));
        }
        /*
            进行文件的保存
         */
        for (File file : files) {
            // 生成图片系统信息
            Pic pictureData = generateInfo(file, c);
            if (pictureData == null)
                continue;
            // 存文件
            try {
                FileUtils.copyFile(file, pictureData.getPath());
            } catch (IOException e) {
                log.error("Error When Save Pic " + pictureData.getName() + " : ", e);
                continue;
            }
            // 存数据库
            picMapper.insert(pictureData);

            Picture pictureInfo = new Picture()
                    .setSequence(pictureData.getSequence())
                    .setName(pictureData.getName())
                    .setCollection(c.getCId())
                    .setIndex(Integer.MAX_VALUE);
            pictureMapper.insert(pictureInfo);

            List<Artwork> artworks = new ArrayList<>();
            for (Illustrator i : illustrators) {
                artworks.add(new Artwork()
                        .setIllustrator(i.getId())
                        .setSequence(pictureData.getSequence()));
            }
            if (!artworks.isEmpty())
                illustratorMapper.insertAllArtwork(artworks);

            // 设置缩略图任务
            thumbnailTask.submit(pictureData.getPath(), pictureData.getName(), pictureData.getThumbnailName());
        }
        // 设置index更新任务
        indexTask.submit(c.getCId());
        // 设置时间戳更新任务
        updateTask.submit(c.getCId());

        return StatusCode.OJBK;
    }

    private Pic generateInfo(File file, Collection c) {
        Pic picture = new Pic();
        CollectionBase info = collectionMapper.findBaseInfo(c.getCId());

        String md5Source = info.getGroup() + info.getCollection() + file.getName();
        long sequence = MD5s.getMD5Sequence(md5Source);
        if (sequence == -1)
            return null;
        if (picMapper.find(sequence) != null) {
            log.warn("Duplicate Pic : " + md5Source);
            log.warn("Duplicate Sequence : " + sequence);
            return null;
        }

        picture.setSequence(sequence)
                .setName(file.getName())
                .setSize(file.length())
                .setUploadTime(new Timestamp(System.currentTimeMillis()))
                .setPath(SunSiteConstant.picturePath
                            + info.getType()
                        + SunSiteConstant.pathSeparator
                            + info.getGroup()
                        + SunSiteConstant.pathSeparator
                            + info.getCollection()
                        + SunSiteConstant.pathSeparator);

        //缩略图文件名
        String thumbnailName = Pic.THUMBNAIL_PREFIX + picture.getName();
        Matcher fileNameMatcher = Pattern.compile("\\.(jpg|jpeg)$")
                .matcher(picture.getName());
        if (!fileNameMatcher.find())
            thumbnailName = thumbnailName.substring(
                    0, thumbnailName.lastIndexOf('.')) + ".jpg";
        picture.setThumbnailName(thumbnailName);

        //获取图片长宽
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

    @Override
    public StatusCode delete(long sequence) {
        Pic p = picMapper.find(sequence);
        if (p == null)
            return StatusCode.ILLEGAL_INPUT;

        pictureMapper.delete(sequence);
        picMapper.delete(sequence);
        illustratorMapper.deletePicture(sequence);

        //删除文件本体
        if (!FileUtils.deleteFile(p.getPath() + p.getName())
                | !FileUtils.deleteFile(p.getPath() + p.getThumbnailName()))
            log.warn("Delete Picture Failed : " + p.getPath() + p.getName());

        return StatusCode.OJBK;
    }

    @Override
    public StatusCode deleteCollection(long sequence) {
        List<Picture> pictures = pictureMapper.findAllByCollection(sequence);

        for (Picture p : pictures) {
            picMapper.delete(p.getSequence());
            illustratorMapper.deletePicture(p.getSequence());
        }
        pictureMapper.deleteAllByCollection(sequence);

        return StatusCode.OJBK;
    }
}
