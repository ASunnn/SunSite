package sunnn.sunsite.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sunnn.sunsite.dao.IllustratorMapper;
import sunnn.sunsite.dao.PicMapper;
import sunnn.sunsite.dto.IllustratorInfo;
import sunnn.sunsite.dto.response.IllustratorListResponse;
import sunnn.sunsite.entity.Illustrator;
import sunnn.sunsite.entity.Pic;
import sunnn.sunsite.exception.IllegalFileRequestException;
import sunnn.sunsite.service.IllustratorService;
import sunnn.sunsite.util.*;

import javax.annotation.Resource;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static sunnn.sunsite.util.Utils.isIllegalPageParam;

@Service
public class IllustratorServiceImpl implements IllustratorService {

    private static Logger log = LoggerFactory.getLogger(IllustratorServiceImpl.class);

    private final FileCache fileCache;

    @Resource
    private IllustratorMapper illustratorMapper;

    @Resource
    private PicMapper picMapper;

    @Autowired
    public IllustratorServiceImpl(FileCache fileCache) {
        this.fileCache = fileCache;
    }

    @Override
    public Illustrator createIllustrator(String name) {
        Illustrator illustrator = illustratorMapper.find(name);
        if (illustrator == null)
            illustratorMapper.insert(illustrator = new Illustrator().setName(name));

        return illustrator;
    }

    @Override
    public IllustratorListResponse getIllustratorList(int page) {
        if (isIllegalPageParam(page))
            return new IllustratorListResponse(StatusCode.ILLEGAL_INPUT);

        int size = SunSiteConstant.pageSize;
        int skip = page * size;

        List<IllustratorInfo> illustratorList = illustratorMapper.findAllInfo(skip, size);
        if (illustratorList.isEmpty())
            return new IllustratorListResponse(StatusCode.NO_DATA);

        int count = illustratorMapper.count();
        int pageCount = (int) Math.ceil((double) count / SunSiteConstant.pageSize);

        return new IllustratorListResponse(StatusCode.OJBK)
                .setPageCount(pageCount)
                .convertTo(illustratorList);
    }

    @Override
    public File download(String name) throws IllegalFileRequestException {
        Illustrator i = illustratorMapper.find(name);
        if (i == null)
            throw new IllegalFileRequestException("Illustrator : " + name);

        String tempCode = "download_Illustrator_" + name;

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
        List<Pic> pictureList = illustratorMapper.findAllByIllustrator(name, 0, Integer.MAX_VALUE);
        try {
            if (!FileUtils.createPath(path))
                throw new IOException("Cant not Create Path : " + path);
            for (Pic picture : pictureList) {
                String locate = picture.getPath() + picture.getName();
                FileUtils.copyFile(new File(locate), path);
            }
        } catch (IOException e) {
            log.error("Copy File Error : ", e);
            throw new IllegalFileRequestException("Illustrator : " + name);
        }
        /*
            压缩
         */
        try {
            ZipCompress.compress(path, zipPath);
        } catch (IOException e) {
            log.error("Compress File Error : ", e);
            throw new IllegalFileRequestException("Illustrator : " + name);
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
    public StatusCode delete(String name) {
        Illustrator i = illustratorMapper.find(name);
        if (i == null || i.getName().equals(Illustrator.DEFAULT_VALUE))
            return StatusCode.ILLEGAL_INPUT;

        illustratorMapper.delete(name);

        Illustrator newIllustrator = illustratorMapper.find(Illustrator.DEFAULT_VALUE);
        illustratorMapper.update(newIllustrator.getId(), i.getId());

        return StatusCode.OJBK;
    }
}
