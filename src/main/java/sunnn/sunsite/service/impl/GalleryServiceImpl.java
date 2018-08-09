package sunnn.sunsite.service.impl;

import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sunnn.sunsite.dto.request.PictureListWithFilter;
import sunnn.sunsite.dto.response.PictureInfoResponse;
import sunnn.sunsite.exception.IllegalFileRequestException;
import sunnn.sunsite.util.*;
import sunnn.sunsite.dao.CollectionDao;
import sunnn.sunsite.dao.IllustratorDao;
import sunnn.sunsite.dao.PictureDao;
import sunnn.sunsite.dao.TypeDao;
import sunnn.sunsite.dto.response.PictureListResponse;
import sunnn.sunsite.dto.request.UploadPictureInfo;
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
        if (!checkPageParam(page, pageSize))
            return new PictureListResponse(StatusCode.ILLEGAL_DATA);
        /*
            查询
         */
        List<Picture> pictureList = pictureDao.getPictureList(page, pageSize);
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
    public PictureListResponse getPictureListWithFilter(PictureListWithFilter filter) {
        /*
            参数检查
         */
        if (!checkPageParam(filter.getPage(), filter.getSize()))
            return new PictureListResponse(StatusCode.ILLEGAL_DATA);
        /*
            查询
         */
        BaseDataBoxing<Long> dataCount = new BaseDataBoxing<>(0L);
        List<Picture> pictureList = pictureDao.getPictureList(filter, dataCount);
        if (dataCount.number == 0)
            return new PictureListResponse(StatusCode.NO_DATA);
        int pageCount = (int) Math.ceil(dataCount.number / (double) filter.getSize());
        /*
            转换结果
         */
        PictureListResponse response = new PictureListResponse(StatusCode.OJBK);
        return response.convertTo(pictureList)
                .setPageCount(pageCount);
    }

    private boolean checkPageParam(int page, int size) {
        return !(page < 0 || size < 1);
    }

    public List<Picture> getPictureFromACollection(String illustrator, String collection) {
        return pictureDao.findFromOneCollection(illustrator, collection);
    }

    @Override
    public File getThumbnail(long sequenceCode) {
        Picture picture = pictureDao.findOne(sequenceCode);
        String path = picture.getPath() + picture.getThumbnailName();
        return new File(path);
    }

    @Override
    public PictureInfoResponse getPictureInfo(long sequenceCode, boolean extra) {
        Picture p = pictureDao.findOne(sequenceCode);
        if (p == null)
            return new PictureInfoResponse(StatusCode.ILLEGAL_DATA);

        PictureInfoResponse response = new PictureInfoResponse(StatusCode.OJBK);
        if (extra) {
            long[] s = getClosePicture(p);
            response.setPrev(s[0])
                    .setNext(s[1]);
        }
        return response.setName(p.getFileName())
                .setIllustrator(p.getIllustrator().getName())
                .setCollection(p.getCollection().getName());
    }

    private long[] getClosePicture(Picture p) {
        List<Picture> pictures = getPictureFromACollection(
                p.getIllustrator().getName(), p.getCollection().getName());

        int index = pictures.indexOf(p);

        long[] s = new long[2];
        s[0] = index == 0 ? -1 : pictures.get(index - 1).getSequence();
        s[1] = index + 1 == pictures.size() ? -1 : pictures.get(index + 1).getSequence();
        return s;
    }

    @Override
    public File getPictureFile(String illustrator, String collection, String fileName)
            throws IllegalFileRequestException {
        Picture picture = pictureDao.getPicture(illustrator, collection, fileName);
        if (picture == null)
            throw new IllegalFileRequestException(
                    SunSiteConstant.getPicturePath()
                    + illustrator
                    + SunSiteConstant.pathSeparator
                    + collection
                    + SunSiteConstant.pathSeparator
                    + fileName);
        String path = picture.getPath() + picture.getFileName();
        return new File(path);
    }

    @Override
    public void getGalleryInfo(List<String> illustrator, List<String> collection, List<String> type) {
        pictureDao.getGalleryInfo(illustrator, collection, type);
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

    /**
     * 此方法不支持多线程环境！！！！！！！
     */
    @Override
    public StatusCode saveUpload(UploadPictureInfo info) {
        /*
            获取上传的文件
         */
        List<File> files = fileCache.getFile(info.getUploadCode());
        if (files == null)
            return StatusCode.UPLOAD_TIMEOUT;
        /*
            图片信息处理
         */
        Picture picture = checkInfo(info);
        if (picture == null)
            return StatusCode.ILLEGAL_DATA;
        /*
            进行文件的保存
         */
        //对文件记录进行保存
        for (File file : files) {
            /*
                生成图片系统信息
             */
            long sequence = MD5s.getMD5Sequence(
                    info.getIllustrator() + info.getCollection() + file.getName());
            if (sequence == -1)
                continue;
            if (pictureDao.findOne(sequence) != null) {
                log.warn("Duplicate Picture : "
                        + info.getIllustrator() + info.getCollection() + file.getName());
                log.warn("Duplicate Sequence : " + sequence);
                continue;
            }
            picture.setUploadTime(System.currentTimeMillis())
                    .setSequence(sequence)
                    .setFileName(file.getName())
                    .setPath(SunSiteConstant.picturePath
                            + picture.getIllustrator().getName()
                            + SunSiteConstant.pathSeparator
                            + picture.getCollection().getName()
                            + SunSiteConstant.pathSeparator)
                    .setId(null);   //不想一次次构建pic实体类的话就每次插入前先把id置为null
            /*
                存文件
             */
            try {
                //判断保存路径是否存在
                if (!FileUtils.createPath(picture.getPath()))
                    throw new IOException("Cannot Create Picture Path");

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
            } catch (IOException e) {
                //若中间出错直接返回
                log.error("Error At SaveUpload : ", e);
                return StatusCode.ERROR;
            }
            /*
                存数据库
             */
            pictureDao.insert(picture);
        }
        return StatusCode.OJBK;
    }

    /**
     * 检查文件信息
     *
     * @param info 图片信息
     * @return 生成的图片实体，
     * 当返回null时，表示文件信息错误
     */
    private Picture checkInfo(UploadPictureInfo info) {
        /*
            检查是否为新的画册
         */
        Collection collection = collectionDao.findOne(info.getCollection());
        Type type = typeDao.findOne(info.getType());
        if (collection == null) {
            /*
                检查是否为新的图片类型
             */
            if (type == null) {
                type = new Type(info.getType());
                typeDao.insert(type);
            }
            collection = new Collection(info.getCollection(), type);
            collectionDao.insert(collection);
        } else {
            /*
                非新画册则对图片类型进行校验
             */
            String typeName = collection.getType().getName();
            if (!typeName.equals(info.getType()))
                return null;
        }
        /*
            检查是否为新画师
         */
        Illustrator illustrator = illustratorDao.findOne(info.getIllustrator());
        if (illustrator == null) {
            illustrator = new Illustrator(info.getIllustrator());
            illustratorDao.insert(illustrator);
        }
        /*
            生成图片信息
         */
        return new Picture()
                .setIllustrator(illustrator)
                .setCollection(collection)
                .setType(type);
    }

    @Override
    public StatusCode deletePicture(long sequenceCode) {
        /*
            检查文件
         */
        Picture p = pictureDao.findOne(sequenceCode);
        if (p == null)
            return StatusCode.ILLEGAL_DATA;
        /*
            删除图片
         */
        if (!pictureDao.delete(sequenceCode))
            return StatusCode.DELETE_FAILED;
        //删除文件本体
        if (!FileUtils.deleteFile(p.getPath() + p.getFileName())
                | !FileUtils.deleteFile(p.getPath() + p.getThumbnailName()))
            log.warn("Delete Picture Failed : " + p.getPath() + p.getFileName());
        if (!FileUtils.deletePath(p.getPath()))
            log.warn("Delete Picture Path Failed : " + p.getPath());

        /*
            检查图片关联到的画集和类型
         */
        String collection = p.getCollection().getName();
        if (pictureDao.findByCollection(collection).isEmpty()) {
            //预先保存type
            String type = collectionDao.findOne(collection).getType().getName();
            if (!collectionDao.delete(collection))
                return StatusCode.DELETE_FAILED;

            //检查type是否为空
            if (collectionDao.findByType(type).isEmpty())
                if (!typeDao.delete(type))
                    return StatusCode.DELETE_FAILED;
        }
        /*
            检查图片关联到的绘师
         */
        String illustrator = p.getIllustrator().getName();
        if (pictureDao.findByIllustrator(illustrator).isEmpty()) {
            if (!illustratorDao.delete(illustrator))
                return StatusCode.DELETE_FAILED;
            //删除画师文件夹本体
            if (!FileUtils.deletePathForce(p.getIllustrator().getPath()))
                log.warn("Delete Illustrator Path Failed : " + p.getIllustrator().getPath());
        }

        return StatusCode.OJBK;
    }

}
