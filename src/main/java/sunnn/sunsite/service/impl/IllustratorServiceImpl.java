package sunnn.sunsite.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sunnn.sunsite.dao.CollectionDao;
import sunnn.sunsite.dao.IllustratorDao;
import sunnn.sunsite.dao.PictureDao;
import sunnn.sunsite.dao.TypeDao;
import sunnn.sunsite.entity.Illustrator;
import sunnn.sunsite.exception.IllegalFileRequestException;
import sunnn.sunsite.service.PictureInfoService;
import sunnn.sunsite.util.*;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class IllustratorServiceImpl implements PictureInfoService {

    private static Logger log = LoggerFactory.getLogger(IllustratorServiceImpl.class);

    private final IllustratorDao illustratorDao;

    private final PictureDao pictureDao;

    private final CollectionDao collectionDao;

    private final TypeDao typeDao;

    private final FileCache fileCache;

    @Autowired
    public IllustratorServiceImpl(IllustratorDao illustratorDao, PictureDao pictureDao, CollectionDao collectionDao, TypeDao typeDao, FileCache fileCache) {
        this.illustratorDao = illustratorDao;
        this.pictureDao = pictureDao;
        this.collectionDao = collectionDao;
        this.typeDao = typeDao;
        this.fileCache = fileCache;
    }

    @Override
    public List<Illustrator> getList() {
        return illustratorDao.getAllIllustrator();
    }

    @Override
    public List<String> getRelatedList(String name) {
        return pictureDao.getRelatedList(
                name, "illustrator.name", "collection.name");
    }

    @Override
    public File download(String name) throws IllegalFileRequestException {
        /*
            非法请求判断
         */
        Illustrator illustrator = illustratorDao.findOne(name);
        if (illustrator == null)
            throw new IllegalFileRequestException(
                    SunSiteConstant.picturePath + name);
        /*
            以请求的内容作为缓存的key
            若已有缓存，则直接返回文件
         */
        String tempCode = "download_" + name;
        List<File> files = fileCache.getFile(tempCode);
        if (files != null)     //若成功从缓存中获取到文件，缓存的生命周期会重置，因此不用担心文件被删除
            return files.get(0);
        /*
            新建缓存
         */
        String path = SunSiteConstant.pictureTempPath
                + tempCode
                + SunSiteConstant.pathSeparator
                + illustrator.getName() + ".zip";
        fileCache.setFile(tempCode, new File(path));
        /*
            压缩文件
         */
        try {
            ZipCompress.compress(illustrator.getPath(), path);
        } catch (IOException e) {
            log.error("Compress File Error : ", e);
            return null;
        }
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
        Illustrator i = illustratorDao.findOne(oldName);
        if (i == null)
            return StatusCode.ILLEGAL_DATA;
        if (illustratorDao.findOne(oldName) != null)
            return StatusCode.ILLEGAL_DATA;
        /*
            改文件本体
         */
        String newPath = SunSiteConstant.picturePath + newName + SunSiteConstant.pathSeparator;
        File illustratorFile = new File(i.getPath());
        if (!FileUtils.rename(illustratorFile, newPath)) {
            log.error("Rename Picture Failed : " + i.getPath() + " To " + newPath);
            return StatusCode.RENAME_FAILED;
        }
        /*
            改绘师记录和涉及到的图片记录
         */
        if (!illustratorDao.updateIllustrator(oldName, newName)     //短路原则
                || !pictureDao.updateIllustrator(oldName, newName))
            return StatusCode.RENAME_FAILED;

        return StatusCode.OJBK;
    }

    @Override
    public StatusCode delete(String name) {
        /*
            检查参数
         */
        Illustrator i = illustratorDao.findOne(name);
        if (i == null)
            return StatusCode.ILLEGAL_DATA;
        /*
            删除对应的画师内容
         */
        List<String> collections = getRelatedList(name);
        if (!pictureDao.deleteIllustrator(name) || !illustratorDao.delete(name))    //短路原则
            return StatusCode.DELETE_FAILED;
        //删除文件夹本体
        if (!FileUtils.deletePathForce(i.getPath()))
            log.error("Delete Illustrator Path Error : " + i.getPath());
        /*
            检查图片关联到的画集
         */
        Set<String> types = new HashSet<>();
        for (String collectionName : collections) {
            if (pictureDao.findByCollection(collectionName).isEmpty()) {
                //预先保存type
                types.add(collectionDao.findOne(collectionName).getType().getName());
                if (!collectionDao.delete(collectionName))
                    return StatusCode.DELETE_FAILED;
            }
        }
        /*
            检查画集关联到的类型
         */
        for (String typeName : types) {
            if (collectionDao.findByType(typeName).isEmpty())
                if (!typeDao.delete(typeName))
                    return StatusCode.DELETE_FAILED;
        }

        return StatusCode.OJBK;
    }

    @Override
    public long getThumbnailSequence(String name) {
        return 0;
    }
}
