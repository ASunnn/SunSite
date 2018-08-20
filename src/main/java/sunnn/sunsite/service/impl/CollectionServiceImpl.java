package sunnn.sunsite.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sunnn.sunsite.dao.CollectionDao;
import sunnn.sunsite.dao.PictureDao;
import sunnn.sunsite.entity.Collection;
import sunnn.sunsite.entity.Picture;
import sunnn.sunsite.exception.IllegalFileRequestException;
import sunnn.sunsite.service.PictureInfoService;
import sunnn.sunsite.util.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class CollectionServiceImpl implements PictureInfoService {

    private static Logger log = LoggerFactory.getLogger(IllustratorServiceImpl.class);

    private final CollectionDao collectionDao;

    private final PictureDao pictureDao;

    private final FileCache fileCache;

    @Autowired
    public CollectionServiceImpl(CollectionDao collectionDao, PictureDao pictureDao, FileCache fileCache) {
        this.collectionDao = collectionDao;
        this.pictureDao = pictureDao;
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
//        /*
//            非法请求判断
//         */
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
            新建缓存
         */
        String filePath = SunSiteConstant.tempPath
                + tempCode
                + SunSiteConstant.pathSeparator
                + collection.getName();
        fileCache.setFile(tempCode, new File(filePath));
        fileCache.setFile(tempCode, new File(filePath + ".zip"));
        /*
            收集文件
         */
        List<String> illustratorList = getRelatedList(name);
        for (String i : illustratorList) {
            String srcPath = SunSiteConstant.picturePath + i + name;
//            String destPath = filePath +
//            FileUtils.createPath(new File(srcPath), )
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
            从缓存中获取压缩文件返回
         */
        return fileCache.getFile(tempCode).get(0);
    }

    @Override
    public StatusCode changeName(String oldName, String newName) {
        return null;
    }

    @Override
    public StatusCode delete(String name) {
        return null;
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
