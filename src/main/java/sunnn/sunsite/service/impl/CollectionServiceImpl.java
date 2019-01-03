package sunnn.sunsite.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sunnn.sunsite.dao.CollectionMapper;
import sunnn.sunsite.dto.CollectionBase;
import sunnn.sunsite.dto.CollectionInfo;
import sunnn.sunsite.dto.response.CollectionListResponse;
import sunnn.sunsite.entity.Collection;
import sunnn.sunsite.entity.Group;
import sunnn.sunsite.entity.Type;
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
    private CollectionMapper collectionMapper;

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

        if (collectionMapper.find(cId) != null) {
            log.warn("Duplicate Collection : " + md5Source);
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
        collectionMapper.insert(c);

        updateTask.submit(c.getCId());
        return StatusCode.OJBK;
    }

    @Override
    public CollectionListResponse getCollectionList(int page) {
        if (isIllegalPageParam(page))
            return new CollectionListResponse(StatusCode.ILLEGAL_INPUT);

        int size = SunsiteConstant.pageSize;
        int skip = page * size;

        List<CollectionInfo> collectionList = collectionMapper.findAllInfo(skip, size);
        if (collectionList.isEmpty())
            return new CollectionListResponse(StatusCode.NO_DATA);

        int count = collectionMapper.count();
        int pageCount = (int) Math.ceil((double) count / SunsiteConstant.pageSize);

        return new CollectionListResponse(StatusCode.OJBK)
                .setPageCount(pageCount)
                .convertTo(collectionList);
    }

    @Override
    public CollectionListResponse getCollectionListByGroup(String group) {
        List<CollectionInfo> collectionList = collectionMapper.findAllInfoByGroup(group, 0, Integer.MAX_VALUE);
        if (collectionList.isEmpty())
            return new CollectionListResponse(StatusCode.NO_DATA);

        return new CollectionListResponse(StatusCode.OJBK)
                .convertTo(collectionList);
    }

    @Override
    public CollectionListResponse getCollectionListByType(String type, int page) {
        if (isIllegalPageParam(page))
            return new CollectionListResponse(StatusCode.ILLEGAL_INPUT);

        int size = SunsiteConstant.pageSize;
        int skip = page * size;

        List<CollectionInfo> collectionList = collectionMapper.findAllInfoByType(type, skip, size);
        if (collectionList.isEmpty())
            return new CollectionListResponse(StatusCode.NO_DATA);

        int count = collectionMapper.countByType(type);
        int pageCount = (int) Math.ceil((double) count / SunsiteConstant.pageSize);

        return new CollectionListResponse(StatusCode.OJBK)
                .setPageCount(pageCount)
                .convertTo(collectionList);
    }

    @Override
    public File download(long sequence) throws IllegalFileRequestException {
        Collection c = collectionMapper.find(sequence);
        if (c == null)
            throw new IllegalFileRequestException("Collection : " + sequence);

        CollectionBase info = collectionMapper.findBaseInfo(sequence);

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
    public StatusCode delete(long sequence) {
        Collection c = collectionMapper.find(sequence);
        if (c == null)
            return StatusCode.ILLEGAL_INPUT;

        CollectionBase info = collectionMapper.findBaseInfo(sequence);

        pictureService.deleteCollection(sequence);
        collectionMapper.delete(sequence);

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
