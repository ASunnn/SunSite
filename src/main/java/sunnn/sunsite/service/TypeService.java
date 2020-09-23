package sunnn.sunsite.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sunnn.sunsite.dao.CollectionDao;
import sunnn.sunsite.dao.TypeDao;
import sunnn.sunsite.dto.CollectionInfo;
import sunnn.sunsite.dto.TypeInfo;
import sunnn.sunsite.dto.response.TypeListResponse;
import sunnn.sunsite.entity.Type;
import sunnn.sunsite.exception.IllegalFileRequestException;
import sunnn.sunsite.util.*;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class TypeService {

    private static Logger log = LoggerFactory.getLogger(TypeService.class);

    private CollectionService collectionService;

    private final FileCache fileCache;

    @Resource
    private TypeDao typeDao;

    @Resource
    private CollectionDao collectionDao;

    @Autowired
    public TypeService(FileCache fileCache) {
        this.fileCache = fileCache;
    }

    @Autowired
    public TypeService setCollectionService(CollectionService collectionService) {
        this.collectionService = collectionService;
        return this;
    }

    @Transactional(propagation = Propagation.REQUIRED,
            isolation = Isolation.DEFAULT)
    public Type createType(String name) {
        Type type = typeDao.find(name);
        if (type == null)
            typeDao.insert(type = new Type().setName(name));
        return type;
    }

    public TypeListResponse getTypeList() {
        List<TypeInfo> typeList = typeDao.findAllInfo();
        if (typeList.isEmpty())
            return new TypeListResponse(StatusCode.NO_DATA);

        return new TypeListResponse(StatusCode.OJBK)
                .setTypeList((TypeInfo[]) typeList.toArray());
    }

    public File download(String name) throws IllegalFileRequestException {
        Type t = typeDao.find(name);
        if (t == null)
            throw new IllegalFileRequestException("Type : " + name);

        String tempCode = "download_type_" + name;
        List<File> files = fileCache.getFile(tempCode);
        if (files != null)     //若成功从缓存中获取到文件，缓存的生命周期会重置，因此不用担心文件被删除
            return files.get(0);

        // 压缩文件
        String path = SunSiteProperties.savePath + name;
        String zipPath = SunSiteProperties.tempPath + tempCode + File.separator + name + ".zip";
        try {
            ZipCompress.compress(path, zipPath);
        } catch (IOException e) {
            log.error("Compress File Error : ", e);
            throw new IllegalFileRequestException("Type : " + name);
        }
        // 新建缓存
        fileCache.setFile(tempCode, new File(zipPath));
        // 从缓存中获取压缩文件返回
        return fileCache.getFile(tempCode).get(0);
    }

    @Transactional(propagation = Propagation.REQUIRED,
            isolation = Isolation.DEFAULT)
    public StatusCode delete(String name) {
        Type t = typeDao.find(name);
        if (t == null)
            return StatusCode.DELETE_FAILED;

        List<CollectionInfo> collectionList = collectionDao.findAllInfoByType(name, 0, Integer.MAX_VALUE);
        for (CollectionInfo collection : collectionList) {
            collectionService.delete(collection.getSequence());
        }
        typeDao.delete(name);

        //删除文件本体
        String path = SunSiteProperties.savePath + name;
        if (!FileUtils.deletePathForce(path))
            log.warn("Delete Collection Failed : " + path);

        return StatusCode.OJBK;
    }
}
