package sunnn.sunsite.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sunnn.sunsite.dao.PictureDao;
import sunnn.sunsite.message.StatusCode;
import sunnn.sunsite.message.request.PicInfo;
import sunnn.sunsite.service.GalleryService;

import javax.servlet.http.HttpSession;

@Service
public class GalleryServiceImpl implements GalleryService {

    @Autowired
    private PictureDao pictureDao;

    @Override
    public StatusCode checkFile(MultipartFile file, HttpSession session) {
        if(file == null || file.isEmpty())
            return StatusCode.EMPTY_UPLOAD;
        if(pictureDao.findOne(file.getOriginalFilename()) != null)
            return StatusCode.DUPLICATED_FILENAME;
        //TODO 把图片放入缓存

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
        return false;
    }
}
