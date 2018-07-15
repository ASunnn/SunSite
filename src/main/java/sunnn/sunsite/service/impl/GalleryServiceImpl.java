package sunnn.sunsite.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sunnn.sunsite.dao.CollectionDao;
import sunnn.sunsite.dao.IllustratorDao;
import sunnn.sunsite.dao.PictureDao;
import sunnn.sunsite.dao.TypeDao;
import sunnn.sunsite.dto.FileCache;
import sunnn.sunsite.dto.StatusCode;
import sunnn.sunsite.dto.request.PicInfo;
import sunnn.sunsite.entity.Collection;
import sunnn.sunsite.entity.Illustrator;
import sunnn.sunsite.entity.Picture;
import sunnn.sunsite.entity.Type;
import sunnn.sunsite.service.GalleryService;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.List;

@Service
public class GalleryServiceImpl implements GalleryService {

    private PictureDao pictureDao;

    private IllustratorDao illustratorDao;

    private CollectionDao collectionDao;

    private TypeDao typeDao;

    private FileCache fileCache;

    @Autowired
    public GalleryServiceImpl(PictureDao pictureDao, IllustratorDao illustratorDao, CollectionDao collectionDao, TypeDao typeDao, FileCache fileCache) {
        this.pictureDao = pictureDao;
        this.illustratorDao = illustratorDao;
        this.collectionDao = collectionDao;
        this.typeDao = typeDao;
        this.fileCache = fileCache;
    }

    @Override
    public StatusCode checkFile(MultipartFile file, String uploadCode) {
        /*
            检测文件是否为空
         */
        if (file == null || file.isEmpty())
            return StatusCode.EMPTY_UPLOAD;
        /*
            查重处理
         */
        if (pictureDao.findOne(file.getOriginalFilename()) != null)
            return StatusCode.DUPLICATED_FILENAME;
        //TODO 对文件名进行处理：没有后缀名的、不支持的图像格式返回错误
        /*
            将图片放入缓存
         */
        try {
            fileCache.setFile(uploadCode, file);
        } catch (IOException e) {
            //发生IO错误的情况下，直接返回
            return StatusCode.ERROR;
        }


        return StatusCode.OJBK;
    }

    @Override
    public boolean saveUpload(PicInfo info) {
        /*
            先进行文件的保存
         */
        //获取上传的文件
        List<File> files = fileCache.getFile(info.getUploadCode());
        for (File file : files) {
            try {
                //保存原图
                FileChannel inputChannel = new FileInputStream(file).getChannel();
                FileChannel outputChannel = new FileOutputStream(new File("D:\\" + file.getName()))
                        .getChannel();
                outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
                inputChannel.close();
                outputChannel.close();
                //TODO 生成缩略图
                //TODO 将图片记录保存到数据库
            } catch (IOException e) {
                //若中间出错，直接报错
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
    private Picture checkInfo(PicInfo info) {
//        if (info.getIllustrator().equals("") ||
//                info.getCollection().equals("") ||
//                info.getType().equals(""))
//            return StatusCode.ILLEGAL_DATA;
        /*
            检查是否为新画师
         */
        Illustrator illustrator = illustratorDao.findOne(info.getIllustrator());
        if (illustrator == null)
            illustrator = new Illustrator(info.getIllustrator());
        /*
            检查是否为新的图片类型
         */
        Type type = typeDao.findOne(info.getType());
        if (type == null)
            type = new Type(info.getType());
        /*
            检查是否为新的画册
         */
        Collection collection = collectionDao.findOne(info.getCollection());
        if (collection == null)
            collection = new Collection(info.getCollection(), type);
        /*
            生成图片信息
         */
        Picture picture = new Picture();
        picture.setIllustrator(illustrator);
        picture.setCollection(collection);
        return picture;
    }
}
