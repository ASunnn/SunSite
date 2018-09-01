package sunnn.sunsite.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sunnn.sunsite.dao.CollectionDao;
import sunnn.sunsite.dao.IllustratorDao;
import sunnn.sunsite.dao.PictureDao;
import sunnn.sunsite.dao.TypeDao;
import sunnn.sunsite.dto.BaseDataBoxing;
import sunnn.sunsite.dto.response.PictureListResponse;
import sunnn.sunsite.entity.Collection;
import sunnn.sunsite.entity.Illustrator;
import sunnn.sunsite.entity.Picture;
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

    private final TypeDao typeDao;

    private final FileCache fileCache;

    @Autowired
    public PoolServiceImpl(IllustratorDao illustratorDao, CollectionDao collectionDao, PictureDao pictureDao, FileCache fileCache, TypeDao typeDao) {
        this.illustratorDao = illustratorDao;
        this.collectionDao = collectionDao;
        this.pictureDao = pictureDao;
        this.fileCache = fileCache;
        this.typeDao = typeDao;
    }

    @Override
    public List<Picture> getPictureFromPool(String illustrator, String collection) {
        return pictureDao.findByPool(illustrator, collection);
    }

    @Override
    public PictureListResponse getPictureFromPool(String illustrator, String collection, int page, int pageSize) {
        if (Utils.isIllegalPageParam(page, pageSize))
            return new PictureListResponse(StatusCode.ILLEGAL_DATA);
        /*
            查询
         */
        BaseDataBoxing<Long> count = new BaseDataBoxing<>(0L);
        List<Picture> pictureList = pictureDao.findByPool(illustrator, collection, page, pageSize, count);
        if (count.number == 0)
            return new PictureListResponse(StatusCode.NO_DATA);
        int pageCount = (int) Math.ceil(count.number / (double) pageSize);
        /*
            转换结果
         */
        PictureListResponse response = new PictureListResponse(StatusCode.OJBK);
        return response.convertTo(pictureList)
                .setPageCount(pageCount);
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
                + illustrator.getName() + " - " + collection.getName();
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
        /*
            数据检查
         */
        Illustrator illustrator = illustratorDao.findOne(illustratorName);
        Collection collection = collectionDao.findOne(collectionName);
        if (illustrator == null || collection == null)
            return StatusCode.ILLEGAL_DATA;
        /*
            删除数据库记录
         */
        if (!pictureDao.deletePool(illustrator.getName(), collection.getName()))
            return StatusCode.DELETE_FAILED;
        /*
            检查画师画集
         */
        if (pictureDao.findByCollection(collectionName).isEmpty()) {
            if (!collectionDao.delete(collectionName))
                return StatusCode.DELETE_FAILED;
            //检查关联的type
            String type = collection.getType().getName();
            if (collectionDao.findByType(type).isEmpty())
                if (!typeDao.delete(type))
                    return StatusCode.DELETE_FAILED;
        }
        if (pictureDao.findByIllustrator(illustratorName).isEmpty())
            if (!illustratorDao.delete(illustratorName))
                return StatusCode.DELETE_FAILED;
        /*
            删除文件本体
         */
        String path = illustrator.getPath() + collectionName;
        if (!FileUtils.deletePathForce(path) || !FileUtils.deletePath(illustrator.getPath()))
            log.error("Delete Pool Error : " + path);

        return StatusCode.OJBK;
    }
}
