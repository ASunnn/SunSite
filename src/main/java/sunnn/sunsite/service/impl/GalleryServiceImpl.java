package sunnn.sunsite.service.impl;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sunnn.sunsite.dao.PictureDao;
import sunnn.sunsite.message.StatusCode;
import sunnn.sunsite.message.request.PicInfo;
import sunnn.sunsite.service.GalleryService;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;

@Service
public class GalleryServiceImpl implements GalleryService {

    @Autowired
    private PictureDao pictureDao;

    @Autowired
    private CacheManager cacheManager;

    @Override
    public StatusCode checkFile(MultipartFile file, HttpSession session) {
        if(file == null || file.isEmpty())
            return StatusCode.EMPTY_UPLOAD;

        if(pictureDao.findOne(file.getOriginalFilename()) != null)
            return StatusCode.DUPLICATED_FILENAME;

        //TODO 把图片放入缓存
        Cache cache = cacheManager.getCache("fileCache");
        Element e = new Element(session, file);
        cache.put(e);

        return StatusCode.OJBK;
    }

    @Override
    public StatusCode checkInfo(PicInfo info) {
        if (info.getIllustrator().equals("") ||
                info.getCollection().equals("") ||
                info.getType().equals(""))
            return StatusCode.ILLEGAL_DATA;
        //TODO 数据检查


        return StatusCode.OJBK;
    }

    @Override
    public boolean saveUpload(HttpSession session) {
        Cache cache = cacheManager.getCache("fileCache");
        Element e = cache.get(session);
        MultipartFile file = (MultipartFile) e.getObjectValue();
        File dir = new File("D:\\" + file.getOriginalFilename());
        try {
            file.transferTo(dir);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
