package sunnn.sunsite.service.impl;

import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sun.security.provider.Sun;
import sunnn.sunsite.util.SunSiteConstant;
import sunnn.sunsite.dao.CollectionDao;
import sunnn.sunsite.dao.IllustratorDao;
import sunnn.sunsite.dao.PictureDao;
import sunnn.sunsite.dao.TypeDao;
import sunnn.sunsite.dto.response.PictureListResponse;
import sunnn.sunsite.util.FileCache;
import sunnn.sunsite.util.StatusCode;
import sunnn.sunsite.dto.request.PictureInfo;
import sunnn.sunsite.entity.Collection;
import sunnn.sunsite.entity.Illustrator;
import sunnn.sunsite.entity.Picture;
import sunnn.sunsite.entity.Type;
import sunnn.sunsite.service.GalleryService;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GalleryServiceImpl implements GalleryService {

    private static Logger log = LoggerFactory.getLogger(GalleryServiceImpl.class);

    private final PictureDao pictureDao;

    private final IllustratorDao illustratorDao;

    private final CollectionDao collectionDao;

    private final TypeDao typeDao;

    private final FileCache fileCache;

    @Autowired
    public GalleryServiceImpl(PictureDao pictureDao, IllustratorDao illustratorDao, CollectionDao collectionDao, TypeDao typeDao, FileCache fileCache) {
        this.pictureDao = pictureDao;
        this.illustratorDao = illustratorDao;
        this.collectionDao = collectionDao;
        this.typeDao = typeDao;
        this.fileCache = fileCache;
    }

    @Override
    public PictureListResponse getPictureList(int page, int pageSize) {
        /*
            参数检查
         */
        if (page < 0 || pageSize < 1)
            return new PictureListResponse(StatusCode.ILLEGAL_DATA);
        /*
            查询
         */
        List<Picture> pictureList = pictureDao.getPicture(page, pageSize);
        if (pictureList.isEmpty())
            return new PictureListResponse(StatusCode.NO_DATA);
        long count = pictureDao.count();
        int pageCount = (int) Math.ceil(count / (double) pageSize);
        /*
            转换结果
         */
        PictureListResponse response = new PictureListResponse(StatusCode.OJBK);
        return response.convertTo(pictureList)
                .setPageCount(pageCount);
    }

    @Override
    public File getThumbnail(String pictureName) {
        Picture picture = pictureDao.findOne(pictureName);
        String path = picture.getPath() + picture.getThumbnailName();
        return new File(path);
    }

    @Override
    public Picture getPictureInfo(String pictureName) {
        return pictureDao.findOne(pictureName);
    }

    @Override
    public File getPictureFile(String pictureName) {
        Picture picture = pictureDao.findOne(pictureName);
        String path = picture.getPath() + picture.getFileName();
        return new File(path);
    }

    @Override
    public StatusCode checkFile(MultipartFile file, String uploadCode) {
        /*
            检测文件是否为空
         */
        if (file == null || file.isEmpty())
            return StatusCode.ILLEGAL_DATA;
        /*
            对文件名进行检查
         */
        Matcher fileNameMatcher = Pattern.compile("\\.(jpg|jpeg|png)$")
                .matcher(file.getOriginalFilename());
        if (!fileNameMatcher.find())
            return StatusCode.ILLEGAL_DATA;
        /*
            查重处理
         */
        if (pictureDao.findOne(file.getOriginalFilename()) != null)
            return StatusCode.DUPLICATED_FILENAME;
        /*
            将图片放入缓存
         */
        try {
            fileCache.setFile(uploadCode, file);
        } catch (IOException e) {
            log.error("Trans TempFile Failed : " + e);
            //发生IO错误的情况下，直接返回
            return StatusCode.ERROR;
        }
        return StatusCode.OJBK;
    }

    @Override
    public boolean saveUpload(PictureInfo info) {
        /*
            图片信息处理
         */
        Picture picture = checkInfo(info);
        /*
            进行文件的保存
         */
        //获取上传的文件
        List<File> files = fileCache.getFile(info.getUploadCode());
        //对文件记录进行保存
        for (File file : files) {
            try {
                /*
                    存文件
                 */
                //生成图片系统信息
                picture.setUploadTime(System.currentTimeMillis())
                        .setFileName(file.getName())
                        .setPath(SunSiteConstant.picturePath
                                + picture.getIllustrator().getName()
                                + SunSiteConstant.pathSeparator
                                + picture.getCollection().getName()
                                + SunSiteConstant.pathSeparator)
                        .setId(null);   //不想一次次构建pic实体类的话就每次插入前先把id置为null

                //判断保存路径是否存在
                File path = new File(picture.getPath());
                if (!path.exists())
                    if (!path.mkdirs())
                        throw new IOException();

                //生成缩略图
                String thumbnailName = Picture.THUMBNAIL_PREFIX + picture.getFileName();
                //判断是否为非jpg文件
                Matcher fileNameMatcher = Pattern.compile("\\.(jpg|jpeg)$")
                        .matcher(picture.getFileName());
                if (!fileNameMatcher.find())
                    thumbnailName = thumbnailName.substring(
                            0, thumbnailName.lastIndexOf('.')) + ".jpg";
                picture.setThumbnailName(thumbnailName);
                //转换
                Thumbnails.of(file.getPath())
                        .size(SunSiteConstant.thumbnailSize, SunSiteConstant.thumbnailSize)
                        .toFile(picture.getPath() + picture.getThumbnailName());

                //保存原图
                FileChannel inputChannel = new FileInputStream(file).getChannel();
                FileChannel outputChannel = new FileOutputStream(
                        new File(picture.getPath() + picture.getFileName()))
                        .getChannel();
                outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
                inputChannel.close();
                outputChannel.close();
                /*
                    存数据库
                 */
                pictureDao.insert(picture);
            } catch (IOException e) {
                //若中间出错，
                log.error("Error At SaveUpload : ", e);
                return false;
            }
        }
        return true;
    }

    /**
     * 检查文件信息
     *
     * @param info 图片信息
     * @return 生成的图片实体
     */
    private Picture checkInfo(PictureInfo info) {
        /*
            检查是否为新画师
         */
        Illustrator illustrator = illustratorDao.findOne(info.getIllustrator());
        if (illustrator == null) {
            illustrator = new Illustrator(info.getIllustrator());
            illustratorDao.insert(illustrator);
        }
        /*
            检查是否为新的图片类型
         */
        Type type = typeDao.findOne(info.getType());
        if (type == null) {
            type = new Type(info.getType());
            typeDao.insert(type);
        }
        /*
            检查是否为新的画册
         */
        Collection collection = collectionDao.findOne(info.getCollection());
        if (collection == null) {
            collection = new Collection(info.getCollection(), type);
            collectionDao.insert(collection);
        }
        /*
            生成图片信息
         */
        return new Picture()
                .setIllustrator(illustrator)
                .setCollection(collection);
    }

    @Override
    public StatusCode deletePicture(String pictureName) {
        /*
            检查文件
         */
        Picture p = pictureDao.findOne(pictureName);
        if (p == null)
            return StatusCode.ILLEGAL_DATA;
        /*
            删除
         */
        if (pictureDao.delete(pictureName)) {
            delete(p.getPath() + p.getFileName());
            delete(p.getPath() + p.getThumbnailName());

            //检查collection是否为空
            String collection = p.getCollection().getName();
            if (pictureDao.findByCollection(collection).isEmpty()) {
                //预先保存type
                String type = collectionDao.findOne(collection).getType().getName();
                collectionDao.delete(collection);
                delete(p.getPath());

                //检查type是否为空
                if (collectionDao.findByType(type).isEmpty())
                    typeDao.delete(type);
            }

            //检查illustrator是否为空
            String illustrator = p.getIllustrator().getName();
            if (pictureDao.findByIllustrator(illustrator).isEmpty()) {
                illustratorDao.delete(illustrator);
                delete(p.getIllustrator().getPath());
            }
        }
        return StatusCode.OJBK;
    }

    private void delete(String path) {
        if (!new File(path).delete())
            log.warn("Delete Picture Failed : " + path);
    }

}
