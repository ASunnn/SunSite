package sunnn.sunsite.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sunnn.sunsite.dao.CollectionDao;
import sunnn.sunsite.dao.IllustratorDao;
import sunnn.sunsite.dao.PictureDao;
import sunnn.sunsite.dao.TypeDao;
import sunnn.sunsite.dto.FileCache;
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
            return StatusCode.ILLEGAL_DATA;
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
    public boolean saveUpload(PictureInfo info) {
        Picture picture = checkInfo(info);
        /*
            进行文件的保存
         */
        //获取上传的文件
        List<File> files = fileCache.getFile(info.getUploadCode());
        //对文件记录进行保存
        for (File file : files) {
            try {
                //生成图片系统信息
                picture.setUploadTime(System.currentTimeMillis());
                picture.setFileName(file.getName());
                picture.setPath("D:\\sunsite\\"
                        + picture.getIllustrator().getName()
                        + "\\"
                        + picture.getCollection().getName()
                        + "\\");
                picture.setId(null);    //不想一次次构建pic实体类的话就每次插入前先把id置为null

                //保存原图
                //判断路径是否存在
                File path = new File(picture.getPath());
                if (!path.exists())
                    if (!path.mkdirs())
                        throw new IOException();
                //保存
                FileChannel inputChannel = new FileInputStream(file).getChannel();
                FileChannel outputChannel = new FileOutputStream(
                        new File(picture.getPath() + picture.getFileName()))
                        .getChannel();
                outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
                inputChannel.close();
                outputChannel.close();

                //TODO 生成缩略图

                //将图片记录保存到数据库
                pictureDao.insert(picture);
            } catch (IOException e) {
                //若中间出错，直接报错
                e.printStackTrace();
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
        System.out.println(info.toString());
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
        Picture picture = new Picture();
        picture.setIllustrator(illustrator);
        picture.setCollection(collection);
        return picture;
    }
}
