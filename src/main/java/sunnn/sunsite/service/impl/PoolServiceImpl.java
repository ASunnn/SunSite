package sunnn.sunsite.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sunnn.sunsite.dao.CollectionDao;
import sunnn.sunsite.dao.IllustratorDao;
import sunnn.sunsite.dao.PictureDao;
import sunnn.sunsite.entity.Collection;
import sunnn.sunsite.entity.Illustrator;
import sunnn.sunsite.exception.IllegalFileRequestException;
import sunnn.sunsite.service.PoolService;
import sunnn.sunsite.util.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class PoolServiceImpl implements PoolService {

    private static Logger log = LoggerFactory.getLogger(IllustratorServiceImpl.class);

    private final IllustratorDao illustratorDao;

    private final CollectionDao collectionDao;

    private final PictureDao pictureDao;

    private final FileCache fileCache;

    @Autowired
    public PoolServiceImpl(IllustratorDao illustratorDao, CollectionDao collectionDao, PictureDao pictureDao, FileCache fileCache) {
        this.illustratorDao = illustratorDao;
        this.collectionDao = collectionDao;
        this.pictureDao = pictureDao;
        this.fileCache = fileCache;
    }

    @Override
    public File download(String illustratorName, String collectionName) throws IllegalFileRequestException {
        /*
            非法请求判断
         */
        Illustrator illustrator = illustratorDao.findOne(illustratorName);
        Collection collection = collectionDao.findOne(collectionName);
        if (illustrator == null || collection == null)
            throw new IllegalFileRequestException(
                    SunSiteConstant.picturePath + illustratorName + SunSiteConstant.pathSeparator + collectionName);
        /*
            查询缓存中是否已经存在
         */
        String tempCode = "download_pool_" + illustratorName + "-" + collectionName;
        List<File> files = fileCache.getFile(tempCode);
        if (files != null)
            return files.get(0);
        /*
            收集文件
         */
        String filePath = SunSiteConstant.tempPath
                + tempCode
                + SunSiteConstant.pathSeparator
                + illustrator.getName() + "-" + collection.getName();
        String srcPath = SunSiteConstant.picturePath
                + illustrator.getName()
                + SunSiteConstant.pathSeparator
                + collection.getName();
        try {
            if (!FileUtils.copyPath(new File(srcPath), filePath, true)) {
                log.error("Copy File Error : " + srcPath + " To " + filePath);
                return null;
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
    public StatusCode delete(String illustratorName, String collectionName) {
        return null;
    }
}
