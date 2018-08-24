package sunnn.sunsite.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sunnn.sunsite.dao.CollectionDao;
import sunnn.sunsite.dao.IllustratorDao;
import sunnn.sunsite.dao.PictureDao;
import sunnn.sunsite.dao.TypeDao;
import sunnn.sunsite.entity.Collection;
import sunnn.sunsite.entity.Picture;
import sunnn.sunsite.exception.IllegalFileRequestException;
import sunnn.sunsite.service.PictureInfoService;
import sunnn.sunsite.util.*;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@Service
public class CollectionServiceImpl implements PictureInfoService {

    private static Logger log = LoggerFactory.getLogger(IllustratorServiceImpl.class);

    private final CollectionDao collectionDao;

    private final PictureDao pictureDao;

    private final TypeDao typeDao;

    private final IllustratorDao illustratorDao;

    private final FileCache fileCache;

    @Autowired
    public CollectionServiceImpl(CollectionDao collectionDao, PictureDao pictureDao, TypeDao typeDao, IllustratorDao illustratorDao, FileCache fileCache) {
        this.collectionDao = collectionDao;
        this.pictureDao = pictureDao;
        this.typeDao = typeDao;
        this.illustratorDao = illustratorDao;
        this.fileCache = fileCache;
    }

    @Override
    public List<Collection> getList() {
        return collectionDao.getAllCollection();
    }

    @Override
    public List<String> getRelatedList(String name) {
        return pictureDao.getRelatedList(
                name, "collection.name", "illustrator.name");
    }

    @Override
    public File download(String name) throws IllegalFileRequestException {
        /*
            非法请求判断
         */
        Collection collection = collectionDao.findOne(name);
        if (collection == null)
            throw new IllegalFileRequestException("Collection : " + name);
        /*
            以请求的内容作为缓存的key
            若已有缓存，则直接返回文件
         */
        String tempCode = "download_collection_" + name;
        List<File> files = fileCache.getFile(tempCode);
        if (files != null)     //若成功从缓存中获取到文件，缓存的生命周期会重置，因此不用担心文件被删除
            return files.get(0);
        /*
            收集文件
         */
        String filePath = SunSiteConstant.tempPath
                + tempCode
                + SunSiteConstant.pathSeparator
                + collection.getName();
        List<String> illustratorList = getRelatedList(name);
        try {
            for (String i : illustratorList) {
                String srcPath = SunSiteConstant.picturePath + i + SunSiteConstant.pathSeparator + name;

                if (!FileUtils.copyPath(
                        new File(srcPath), filePath + SunSiteConstant.pathSeparator + i, true)) {
                    log.error("Copy File Error : " + srcPath + " To " + filePath);
                    return null;
                }
            }
        } catch (IOException e) {
            log.error("Copy File Error : ", e);
            return null;
        }
        /*
            压缩文件
         */
        try {
            ZipCompress.compress(filePath, filePath + ".zip");
        } catch (IOException e) {
            log.error("Compress File Error : ", e);
            return null;
        }
        /*
            新建缓存
         */
        fileCache.setFile(tempCode, new File(filePath + ".zip"));
        fileCache.setFile(tempCode, new File(filePath));
        /*
            从缓存中获取压缩文件返回
         */
        return fileCache.getFile(tempCode).get(0);
    }

    @Override
    public StatusCode changeName(String oldName, String newName) {
        /*
            检查参数
         */
        if (collectionDao.findOne(oldName) == null)
            return StatusCode.ILLEGAL_DATA;
        if (collectionDao.findOne(newName) != null)
            return StatusCode.ILLEGAL_DATA;
        /*
            改文件本体
         */
        List<String> illustratorList = getRelatedList(oldName);
        for (String i : illustratorList) {
            String oldPath =
                    SunSiteConstant.picturePath + i + SunSiteConstant.pathSeparator + oldName;
            String newPath =
                    SunSiteConstant.picturePath + i + SunSiteConstant.pathSeparator + newName;
            File collectionFile = new File(oldPath);
            if (!FileUtils.rename(collectionFile, newPath)) {  //若中途发生错误必须停止流程
                log.error("Rename Picture Failed : " + oldPath + " To " + newPath);
                return StatusCode.RENAME_FAILED;
            }
        }

        /*
            改画集记录和涉及到的图片记录
         */
        if (!collectionDao.updateCollection(oldName, newName)
                || !pictureDao.updateInfo("collection.name", oldName, newName))
            return StatusCode.RENAME_FAILED;

        return StatusCode.OJBK;
    }

    @Override
    public StatusCode delete(String name) {
        /*
            检查参数
         */
        Collection c = collectionDao.findOne(name);
        if (c == null)
            return StatusCode.ILLEGAL_DATA;
        List<String> illustrators = getRelatedList(name);
        /*
            删除对应的画册内容
         */
        if (!pictureDao.deletePictures("collection.name", name)
                || !collectionDao.delete(name))
            return StatusCode.DELETE_FAILED;
        /*
            检查画集关联到的类型
         */
        String typeName = c.getType().getName();
        if (collectionDao.findByType(typeName).isEmpty())
            if (!typeDao.delete(typeName))
                return StatusCode.DELETE_FAILED;
        /*
            检查图片关联到的画师
         */
        Iterator<String> i = illustrators.iterator();
        while (i.hasNext()) {

            String illustratorName = i.next();

            if (pictureDao.findByIllustrator(illustratorName).isEmpty()) {
                if (!illustratorDao.delete(illustratorName))
                    return StatusCode.DELETE_FAILED;
                //删除绘师对应的文件本体
                String path = SunSiteConstant.picturePath + illustratorName;
                if (!FileUtils.deletePathForce(path))    //中途发生错误可以继续流程
                    log.error("Delete Illustrator Path Error : " + path);

                i.remove();
            }
        }
        /*
            删除各个绘师文件夹下的画册本体
         */
        for (String illustratorName : illustrators) {
            String path = SunSiteConstant.picturePath
                    + illustratorName + SunSiteConstant.pathSeparator + name;
            if (!FileUtils.deletePathForce(path))
                log.error("Delete Collection Path Error : " + path);
        }

        return StatusCode.OJBK;
    }

    @Override
    public long getThumbnailSequence(String name) {
        Picture p = pictureDao.getFirst(name, "collection.name");
        if (p == null) {
            log.warn("Cannot Find Any Picture In Collection : " + name);
            return -1;
        }
        return p.getSequence();
    }
}
