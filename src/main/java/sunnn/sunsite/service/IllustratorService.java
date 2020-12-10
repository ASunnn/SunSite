package sunnn.sunsite.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sunnn.sunsite.dao.IllustratorDao;
import sunnn.sunsite.dto.IllustratorInfo;
import sunnn.sunsite.dto.response.IllustratorInfoResponse;
import sunnn.sunsite.dto.response.IllustratorListResponse;
import sunnn.sunsite.entity.Alias;
import sunnn.sunsite.entity.Illustrator;
import sunnn.sunsite.entity.Pic;
import sunnn.sunsite.exception.IllegalFileRequestException;
import sunnn.sunsite.util.*;

import javax.annotation.Resource;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static sunnn.sunsite.util.Utils.isIllegalPageParam;

@Service
public class IllustratorService {

    private static Logger log = LoggerFactory.getLogger(IllustratorService.class);

    private final AliasService aliasService;

    private final FileCache fileCache;

    @Resource
    private IllustratorDao illustratorDao;

    @Autowired
    public IllustratorService(AliasService aliasService, FileCache fileCache) {
        this.aliasService = aliasService;
        this.fileCache = fileCache;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW,
            isolation = Isolation.DEFAULT)
    public Illustrator createIllustrator(String name) {
        Illustrator illustrator = illustratorDao.find(name);
        if (illustrator == null)
            illustratorDao.insert(illustrator = new Illustrator().setName(name));

        return illustrator;
    }

    public IllustratorListResponse getIllustratorList(int page) {
        if (isIllegalPageParam(page))
            return new IllustratorListResponse(StatusCode.ILLEGAL_INPUT);

        int size = SunsiteConstant.PAGE_SIZE;
        int skip = page * size;

        List<IllustratorInfo> illustratorList = illustratorDao.findAllInfo(skip, size);
        if (illustratorList.isEmpty())
            return new IllustratorListResponse(StatusCode.NO_DATA);

        int count = illustratorDao.count();
        int pageCount = (int) Math.ceil((double) count / SunsiteConstant.PAGE_SIZE);

        return new IllustratorListResponse(StatusCode.OJBK)
                .setPageCount(pageCount)
                .setList(illustratorList.toArray(new IllustratorInfo[0]));
    }

    public IllustratorListResponse getIllustratorList(String query, int page) {
        if (isIllegalPageParam(page))
            return new IllustratorListResponse(StatusCode.ILLEGAL_INPUT);

        int size = SunsiteConstant.PAGE_SIZE;
        int skip = page * size;

        List<IllustratorInfo> illustratorList = illustratorDao.findAllInfoByName(query, skip, size);
        if (illustratorList.isEmpty())
            return new IllustratorListResponse(StatusCode.NO_DATA);

        int count = illustratorDao.countByName(query);
        int pageCount = (int) Math.ceil((double) count / SunsiteConstant.PAGE_SIZE);

        return new IllustratorListResponse(StatusCode.OJBK)
                .setPageCount(pageCount)
                .setList((IllustratorInfo[]) illustratorList.toArray());
    }

    public IllustratorInfoResponse getIllustratorInfo(String name) {
        Illustrator i = illustratorDao.find(name);
        if (i == null)
            return new IllustratorInfoResponse(StatusCode.ILLEGAL_INPUT);

        String[] aliases = aliasService.getAlias(i.getId(), Alias.ILLUSTRATOR);

        StringBuilder alias = new StringBuilder();
        if (aliases.length > 0) {
            for (String a : aliases)
                alias.append(a).append(SunsiteConstant.SEPARATOR);
            alias.deleteCharAt(alias.length() - 1);
        }

        return new IllustratorInfoResponse(StatusCode.OJBK)
                .setIllustrator(i.getName())
                .setAlias(alias.toString());
    }

    public File getIllustratorThumbnail(String name) {
        List<Pic> pictures = illustratorDao.findAllByIllustrator(name, 0, 1);

        if (pictures != null && !pictures.isEmpty()) {
            Pic pic = pictures.get(0);
            File f = new File(SunSiteProperties.savePath + pic.getPath() + pic.getThumbnailName());
            if (f.exists())
                return f;
        }

        File f = new File(SunSiteProperties.missPicture);
        if (f.exists())
            return f;
        log.warn("404.jpg Miss");
        return null;
    }

    public File download(String name) throws IllegalFileRequestException {
        Illustrator i = illustratorDao.find(name);
        if (i == null)
            throw new IllegalFileRequestException("Illustrator : " + name);

        String tempCode = "download_Illustrator_" + name;

        List<File> files = fileCache.getFile(tempCode);
        if (files != null)
            return files.get(0);

        String path = SunSiteProperties.tempPath + tempCode + File.separator + name;
        String zipPath = SunSiteProperties.tempPath + tempCode + File.separator + name + ".zip";

        // 收集文件
        List<Pic> pictureList = illustratorDao.findAllByIllustrator(name, 0, Integer.MAX_VALUE);
        try {
            if (!FileUtils.createPath(path))
                throw new IOException("Cant not Create Path : " + path);
            for (Pic picture : pictureList) {
                String locate = SunSiteProperties.savePath + picture.getPath() + picture.getName();
                FileUtils.copyFile(new File(locate), path);
            }
        } catch (IOException e) {
            log.error("Copy File Error : ", e);
            throw new IllegalFileRequestException("Illustrator : " + name);
        }
        // 压缩
        try {
            ZipCompress.compress(path, zipPath);
        } catch (IOException e) {
            log.error("Compress File Error : ", e);
            throw new IllegalFileRequestException("Illustrator : " + name);
        }

        // 新建缓存
        fileCache.setFile(tempCode, new File(zipPath));
        // 从缓存中获取压缩文件返回
        return fileCache.getFile(tempCode).get(0);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW,
            isolation = Isolation.DEFAULT)
    public StatusCode modifyName(String oldName, String newName) {
        newName = newName.trim();

        if (oldName.equals(newName))
            return StatusCode.OJBK;

        Illustrator i = illustratorDao.find(oldName);
        if (i == null || i.getName().equals(Illustrator.DEFAULT_VALUE))
            return StatusCode.ILLEGAL_INPUT;
        if (illustratorDao.find(newName) != null)
            return StatusCode.DUPLICATE_INPUT;

        illustratorDao.update(oldName, newName);

        return StatusCode.OJBK;
    }

    public StatusCode modifyAlias(String name, String alias) {
        Illustrator i = illustratorDao.find(name);
        if (i == null)
            return StatusCode.ILLEGAL_INPUT;

        return aliasService.modifyAlias(i.getId(), Alias.ILLUSTRATOR, alias.split(SunsiteConstant.SEPARATOR));
    }

    @Transactional
    public StatusCode delete(String name) {
        Illustrator i = illustratorDao.find(name);
        if (i == null || i.getName().equals(Illustrator.DEFAULT_VALUE))
            return StatusCode.DELETE_FAILED;

        illustratorDao.delete(name);

        Illustrator newIllustrator = illustratorDao.find(Illustrator.DEFAULT_VALUE);
        if (newIllustrator == null)
            newIllustrator = createIllustrator(Illustrator.DEFAULT_VALUE);
        illustratorDao.updateIllustrator(i.getId(), newIllustrator.getId());

        return StatusCode.OJBK;
    }
}
