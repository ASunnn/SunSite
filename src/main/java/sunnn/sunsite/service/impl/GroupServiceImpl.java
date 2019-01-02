package sunnn.sunsite.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sunnn.sunsite.dao.CollectionMapper;
import sunnn.sunsite.dao.GroupMapper;
import sunnn.sunsite.dto.CollectionInfo;
import sunnn.sunsite.dto.GroupInfo;
import sunnn.sunsite.dto.response.GroupListResponse;
import sunnn.sunsite.entity.Group;
import sunnn.sunsite.exception.IllegalFileRequestException;
import sunnn.sunsite.service.CollectionService;
import sunnn.sunsite.service.GroupService;
import sunnn.sunsite.util.*;

import javax.annotation.Resource;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static sunnn.sunsite.util.Utils.isIllegalPageParam;

@Service
public class GroupServiceImpl implements GroupService {

    private static Logger log = LoggerFactory.getLogger(GroupServiceImpl.class);

    private CollectionService collectionService;

    private final FileCache fileCache;

    @Resource
    private GroupMapper groupMapper;

    @Resource
    private CollectionMapper collectionMapper;

    @Autowired
    public GroupServiceImpl(FileCache fileCache) {
        this.fileCache = fileCache;
    }

    /**
     * 解决这俩货之间的循环依赖
     */
    @Autowired
    public GroupServiceImpl setCollectionService(CollectionService collectionService) {
        this.collectionService = collectionService;
        return this;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,
            isolation = Isolation.DEFAULT)
    public Group createGroup(String name) {
        Group group = groupMapper.find(name);
        if (group == null)
            groupMapper.insert(group = new Group().setName(name));
        return group;
    }

    @Override
    public GroupListResponse getGroupList(int page) {
        if (isIllegalPageParam(page))
            return new GroupListResponse(StatusCode.ILLEGAL_INPUT);

        int size = SunSiteConstant.pageSize;
        int skip = page * size;

        List<GroupInfo> collectionList = groupMapper.findAllInfo(skip, size);
        if (collectionList.isEmpty())
            return new GroupListResponse(StatusCode.NO_DATA);

        int count = groupMapper.count();
        int pageCount = (int) Math.ceil((double) count / SunSiteConstant.pageSize);

        return new GroupListResponse(StatusCode.OJBK)
                .setPageCount(pageCount)
                .convertTo(collectionList);
    }

    @Override
    public File download(String name) throws IllegalFileRequestException {
        Group g = groupMapper.find(name);
        if (g == null)
            throw new IllegalFileRequestException("Group : " + name);

        String tempCode = "download_group_" + name;

        List<File> files = fileCache.getFile(tempCode);
        if (files != null)
            return files.get(0);

        String path = SunSiteConstant.tempPath
                + tempCode
                + SunSiteConstant.pathSeparator
                + name;
        String zipPath = SunSiteConstant.tempPath
                + tempCode
                + SunSiteConstant.pathSeparator
                + name
                + ".zip";
        /*
            收集文件
         */
        List<CollectionInfo> collectionList = collectionMapper.findAllInfoByGroup(name, 0, Integer.MAX_VALUE);
        try {
            for (CollectionInfo collection : collectionList) {
                String locate = SunSiteConstant.picturePath
                        + collection.getType()
                        + SunSiteConstant.pathSeparator
                        + collection.getGroup()
                        + SunSiteConstant.pathSeparator
                        + collection.getCollection();
                if (!FileUtils.copyPath(new File(locate), path, false))
                    log.error("Copy File Error : " + locate + " To " + path);
            }
        } catch (IOException e) {
            log.error("Copy File Error : ", e);
            throw new IllegalFileRequestException("Group : " + name);
        }
        /*
            压缩
         */
        try {
            ZipCompress.compress(path, zipPath);
        } catch (IOException e) {
            log.error("Compress File Error : ", e);
            throw new IllegalFileRequestException("Group : " + name);
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
    @Transactional(propagation = Propagation.REQUIRED,
            isolation = Isolation.DEFAULT)
    public StatusCode delete(String name) {
        Group g = groupMapper.find(name);
        if (g == null)
            return StatusCode.ILLEGAL_INPUT;

        List<CollectionInfo> collectionList = collectionMapper.findAllInfoByGroup(name, 0, Integer.MAX_VALUE);
        for (CollectionInfo collection : collectionList) {
            collectionService.delete(collection.getSequence());
        }
        groupMapper.delete(name);

        return StatusCode.OJBK;
    }
}
