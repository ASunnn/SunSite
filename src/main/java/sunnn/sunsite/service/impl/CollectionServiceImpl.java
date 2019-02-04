package sunnn.sunsite.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sunnn.sunsite.dao.CollectionDao;
import sunnn.sunsite.dao.PicDao;
import sunnn.sunsite.dao.PictureDao;
import sunnn.sunsite.dto.CollectionBase;
import sunnn.sunsite.dto.CollectionInfo;
import sunnn.sunsite.dto.response.CollectionInfoResponse;
import sunnn.sunsite.dto.response.CollectionListResponse;
import sunnn.sunsite.entity.*;
import sunnn.sunsite.exception.IllegalFileRequestException;
import sunnn.sunsite.service.CollectionService;
import sunnn.sunsite.service.GroupService;
import sunnn.sunsite.service.PictureService;
import sunnn.sunsite.service.TypeService;
import sunnn.sunsite.task.TimeUpdateTask;
import sunnn.sunsite.util.*;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import static sunnn.sunsite.util.Utils.isIllegalPageParam;

@Service
public class CollectionServiceImpl implements CollectionService {

    private static Logger log = LoggerFactory.getLogger(CollectionServiceImpl.class);

    private final PictureService pictureService;

    private GroupService groupService;

    private TypeService typeService;

    private final FileCache fileCache;

    @Resource
    private CollectionDao collectionDao;

    @Resource
    private PictureDao pictureDao;

    @Resource
    private PicDao picDao;

    private final TimeUpdateTask updateTask;

    @Autowired
    public CollectionServiceImpl(TimeUpdateTask updateTask, PictureService pictureService, FileCache fileCache) {
        this.updateTask = updateTask;
        this.pictureService = pictureService;
        this.fileCache = fileCache;
    }

    @Autowired
    public CollectionServiceImpl setGroupService(GroupService groupService) {
        this.groupService = groupService;
        return this;
    }

    @Autowired
    public CollectionServiceImpl setTypeService(TypeService typeService) {
        this.typeService = typeService;
        return this;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,
            isolation = Isolation.DEFAULT)
    public StatusCode createCollection(CollectionBase info) {
        /*
            非法字符校验
         */
        if (FileUtils.fileNameMatcher(info.getCollection())
                || FileUtils.fileNameMatcher(info.getGroup())
                || FileUtils.fileNameMatcher(info.getType()))
            return StatusCode.ILLEGAL_INPUT;
        /*
            处理type和group
         */
        Group group = groupService.createGroup(info.getGroup());
        Type type = typeService.createType(info.getType());
        /*
            生成序列号
         */
        String md5Source = info.getGroup() + info.getCollection();
        long cId = MD5s.getMD5Sequence(md5Source);

        if (collectionDao.find(cId) != null) {
            log.warn("Duplicate Collection : " + info.getGroup() + " - " + info.getCollection());
            log.warn("Duplicate Sequence : " + cId);
            return StatusCode.DUPLICATE_INPUT;
        }
        /*
            保存
         */
        // 创建目录
        String path = SunSiteProperties.savePath
                + info.getType()
                + File.separator
                + info.getGroup()
                + File.separator
                + info.getCollection();
        FileUtils.createPath(path);

        Collection c = new Collection()
                .setCId(cId)
                .setName(info.getCollection())
                .setGroup(group.getId())
                .setType(type.getId())
                .setCreateTime(new Timestamp(System.currentTimeMillis()));
        collectionDao.insert(c);

        updateTask.submit(c.getCId());
        return StatusCode.OJBK;
    }

    @Override
    public CollectionListResponse getCollectionList(int page) {
        if (isIllegalPageParam(page))
            return new CollectionListResponse(StatusCode.ILLEGAL_INPUT);

        int size = SunsiteConstant.PAGE_SIZE;
        int skip = page * size;

        List<CollectionInfo> collectionList = collectionDao.findAllInfo(skip, size);
        if (collectionList.isEmpty())
            return new CollectionListResponse(StatusCode.NO_DATA);

        int count = collectionDao.count();
        int pageCount = (int) Math.ceil((double) count / SunsiteConstant.PAGE_SIZE);

        return new CollectionListResponse(StatusCode.OJBK)
                .setPageCount(pageCount)
                .convertTo(collectionList);
    }

    @Override
    public CollectionListResponse getCollectionListByGroup(String group) {
        List<CollectionInfo> collectionList = collectionDao.findAllInfoByGroup(group, 0, Integer.MAX_VALUE);
        if (collectionList.isEmpty())
            return new CollectionListResponse(StatusCode.NO_DATA);

        return new CollectionListResponse(StatusCode.OJBK)
                .convertTo(collectionList);
    }

    @Override
    public CollectionListResponse getCollectionListByType(String type, int page) {
        if (isIllegalPageParam(page))
            return new CollectionListResponse(StatusCode.ILLEGAL_INPUT);

        int size = SunsiteConstant.PAGE_SIZE;
        int skip = page * size;

        List<CollectionInfo> collectionList = collectionDao.findAllInfoByType(type, skip, size);
        if (collectionList.isEmpty())
            return new CollectionListResponse(StatusCode.NO_DATA);

        int count = collectionDao.countByType(type);
        int pageCount = (int) Math.ceil((double) count / SunsiteConstant.PAGE_SIZE);

        return new CollectionListResponse(StatusCode.OJBK)
                .setPageCount(pageCount)
                .convertTo(collectionList);
    }

    @Override
    public CollectionInfoResponse getCollectionInfo(long sequence) {
        CollectionBase baseInfo = collectionDao.findBaseInfo(sequence);
        if (baseInfo == null)
            return new CollectionInfoResponse(StatusCode.ILLEGAL_INPUT);

        return new CollectionInfoResponse(StatusCode.OJBK)
                .setGroup(baseInfo.getGroup())
                .setCollection(baseInfo.getCollection())
                .setType(baseInfo.getType());
    }

    @Override
    public File download(long sequence) throws IllegalFileRequestException {
        Collection c = collectionDao.find(sequence);
        if (c == null)
            throw new IllegalFileRequestException("Collection : " + sequence);

        CollectionBase info = collectionDao.findBaseInfo(sequence);

        String tempCode = "download_collection_" + sequence;
        List<File> files = fileCache.getFile(tempCode);
        if (files != null)     // 若成功从缓存中获取到文件，缓存的生命周期会重置，因此不用担心文件被删除
            return files.get(0);
        /*
            压缩文件
         */
        String path = SunSiteProperties.savePath
                + info.getType()
                + File.separator
                + info.getGroup()
                + File.separator
                + info.getCollection();
        String zipPath = SunSiteProperties.tempPath
                + tempCode
                + File.separator
                + info.getGroup()
                + "-"
                + info.getCollection()
                + ".zip";
        try {
            ZipCompress.compress(path, zipPath);
        } catch (IOException e) {
            log.error("Compress File Error : ", e);
            throw new IllegalFileRequestException("Collection : " + sequence);
        }
        /*
            新建缓存
         */
        fileCache.setFile(tempCode, new File(zipPath));
        /*
            从缓存中获取压缩文件返回
         */
        return fileCache.getFile(tempCode).get(0);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW,
            isolation = Isolation.DEFAULT)
    public StatusCode modifyName(long sequence, String newName) {
        // 校验
        Collection c = collectionDao.find(sequence);
        if (FileUtils.fileNameMatcher(newName)
                || c == null)
            return StatusCode.ILLEGAL_INPUT;

        if (c.getName().equals(newName))
            return StatusCode.OJBK;

        CollectionBase baseInfo = collectionDao.findBaseInfo(sequence);
        // 文件操作
        String path = SunSiteProperties.savePath
                + baseInfo.getType()
                + File.separator
                + baseInfo.getGroup()
                + File.separator
                + baseInfo.getCollection();
        boolean result = FileUtils.rename(new File(path), newName);
        if (!result)
            return StatusCode.MODIFY_FAILED;

        // 修改数据库
        collectionDao.updateName(sequence, newName);
        List<Picture> pictureList = pictureDao.findAllByCollection(sequence);
        for (Picture picture : pictureList) {
            Pic p = picDao.find(picture.getSequence());

            String[] elements = p.getPath().split("\\\\");
            if (elements.length == 0)
                elements = p.getPath().split("/");
            String newPath = elements[0]
                    + File.separator
                    + elements[1]
                    + File.separator
                    + newName
                    + File.separator;

            picDao.updatePath(p.getSequence(), newPath);
        }

        return StatusCode.OJBK;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW,
            isolation = Isolation.DEFAULT)
    public StatusCode delete(long sequence) {
        Collection c = collectionDao.find(sequence);
        if (c == null)
            return StatusCode.DELETE_FAILED;

        CollectionBase info = collectionDao.findBaseInfo(sequence);

        pictureService.deleteCollection(sequence);
        collectionDao.delete(sequence);

        String parentPath = SunSiteProperties.savePath
                + info.getType()
                + File.separator
                + info.getGroup();
        String path = parentPath
                + File.separator
                + info.getCollection();
        // 删除文件本体
        if (!FileUtils.deletePathForce(path))
            log.warn("Delete Collection Failed : " + path);
        // 检查父文件夹
        FileUtils.deletePath(parentPath);

        return StatusCode.OJBK;
    }
}
