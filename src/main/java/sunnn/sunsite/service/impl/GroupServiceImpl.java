package sunnn.sunsite.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sunnn.sunsite.dao.CollectionDao;
import sunnn.sunsite.dao.GroupDao;
import sunnn.sunsite.dao.PicDao;
import sunnn.sunsite.dao.PictureDao;
import sunnn.sunsite.dto.CollectionInfo;
import sunnn.sunsite.dto.GroupInfo;
import sunnn.sunsite.dto.response.GroupInfoResponse;
import sunnn.sunsite.dto.response.GroupListResponse;
import sunnn.sunsite.entity.Alias;
import sunnn.sunsite.entity.Group;
import sunnn.sunsite.entity.Pic;
import sunnn.sunsite.entity.Picture;
import sunnn.sunsite.exception.IllegalFileRequestException;
import sunnn.sunsite.service.AliasService;
import sunnn.sunsite.service.CollectionService;
import sunnn.sunsite.service.GroupService;
import sunnn.sunsite.util.*;

import javax.annotation.Resource;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static sunnn.sunsite.util.Utils.isIllegalPageParam;

@Service
public class GroupServiceImpl implements GroupService {

    private static Logger log = LoggerFactory.getLogger(GroupServiceImpl.class);

    private CollectionService collectionService;

    private final AliasService aliasService;

    private final FileCache fileCache;

    @Resource
    private GroupDao groupDao;

    @Resource
    private CollectionDao collectionDao;

    @Resource
    private PictureDao pictureDao;

    @Resource
    private PicDao picDao;

    @Autowired
    public GroupServiceImpl(FileCache fileCache, AliasService aliasService) {
        this.fileCache = fileCache;
        this.aliasService = aliasService;
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
        Group group = groupDao.find(name);
        if (group == null)
            groupDao.insert(group = new Group().setName(name));
        return group;
    }

    @Override
    public GroupListResponse getGroupList(int page) {
        if (isIllegalPageParam(page))
            return new GroupListResponse(StatusCode.ILLEGAL_INPUT);

        int size = SunsiteConstant.PAGE_SIZE;
        int skip = page * size;

        List<GroupInfo> collectionList = groupDao.findAllInfo(skip, size);
        if (collectionList.isEmpty())
            return new GroupListResponse(StatusCode.NO_DATA);

        int count = groupDao.count();
        int pageCount = (int) Math.ceil((double) count / SunsiteConstant.PAGE_SIZE);

        return new GroupListResponse(StatusCode.OJBK)
                .setPageCount(pageCount)
                .convertTo(collectionList);
    }

    @Override
    public GroupInfoResponse getGroupInfo(String name) {
        Group g = groupDao.find(name);
        if (g == null)
            return new GroupInfoResponse(StatusCode.ILLEGAL_INPUT);

        String[] aliases = aliasService.getAlias(g.getId(), Alias.GROUP);

        StringBuilder alias = new StringBuilder();
        if (aliases.length > 0) {
            for (String a : aliases)
                alias.append(a).append(SunsiteConstant.SEPARATOR);
            alias.deleteCharAt(alias.length() - 1);
        }

        return new GroupInfoResponse(StatusCode.OJBK)
                .setGroup(g.getName())
                .setAlias(alias.toString());
    }

    @Override
    public File download(String name) throws IllegalFileRequestException {
        Group g = groupDao.find(name);
        if (g == null)
            throw new IllegalFileRequestException("Group : " + name);

        String tempCode = "download_group_" + name;

        List<File> files = fileCache.getFile(tempCode);
        if (files != null)
            return files.get(0);

        String path = SunSiteProperties.tempPath
                + tempCode
                + File.separator
                + name;
        String zipPath = SunSiteProperties.tempPath
                + tempCode
                + File.separator
                + name
                + ".zip";
        /*
            收集文件
         */
        List<CollectionInfo> collectionList = collectionDao.findAllInfoByGroup(name, 0, Integer.MAX_VALUE);
        try {
            for (CollectionInfo collection : collectionList) {
                String locate = SunSiteProperties.savePath
                        + collection.getType()
                        + File.separator
                        + collection.getGroup()
                        + File.separator
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
    @Transactional(propagation = Propagation.REQUIRES_NEW,
            isolation = Isolation.DEFAULT)
    public StatusCode modifyName(String oldName, String newName) {
        newName = newName.trim();
        // 校验
        if (oldName.equals(newName))
            return StatusCode.OJBK;

        Group g = groupDao.find(oldName);
        if (FileUtils.fileNameMatcher(newName)
                || g == null)
            return StatusCode.ILLEGAL_INPUT;
        if (groupDao.find(newName) != null)
            return StatusCode.DUPLICATE_INPUT;

        // 文件操作
        List<CollectionInfo> collectionList = collectionDao.findAllInfoByGroup(g.getName(), 0, Integer.MAX_VALUE);
        Set<String> typeSet = new HashSet<>();

        for (CollectionInfo collection : collectionList) {
            if (!typeSet.contains(collection.getType())) {
                typeSet.add(collection.getType());

                String path = SunSiteProperties.savePath
                        + collection.getType()
                        + File.separator
                        + oldName;
                boolean result = FileUtils.rename(new File(path), newName);
                if (!result)
                    return StatusCode.MODIFY_FAILED;
            }
        }

        // 修改数据库
        groupDao.updateName(oldName, newName);
        for (CollectionInfo collection : collectionList) {
            List<Picture> pictureList = pictureDao.findAllByCollection(collection.getSequence());
            for (Picture picture : pictureList) {
                Pic p = picDao.find(picture.getSequence());

                String[] elements = p.getPath().split("\\\\");
                if (elements.length == 0)
                    elements = p.getPath().split("/");
                String newPath = elements[0]
                        + File.separator
                        + newName
                        + File.separator
                        + elements[2]
                        + File.separator;

                picDao.updatePath(p.getSequence(), newPath);
            }
        }

        return StatusCode.OJBK;
    }

    @Override
    public StatusCode modifyAlias(String name, String alias) {
        Group g = groupDao.find(name);
        if (g == null)
            return StatusCode.ILLEGAL_INPUT;

        return aliasService.modifyAlias(g.getId(), Alias.GROUP, alias.split(SunsiteConstant.SEPARATOR));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,
            isolation = Isolation.DEFAULT)
    public StatusCode delete(String name) {
        Group g = groupDao.find(name);
        if (g == null)
            return StatusCode.DELETE_FAILED;

        List<CollectionInfo> collectionList = collectionDao.findAllInfoByGroup(name, 0, Integer.MAX_VALUE);
        for (CollectionInfo collection : collectionList) {
            collectionService.delete(collection.getSequence());
        }
        groupDao.delete(name);

        return StatusCode.OJBK;
    }
}
