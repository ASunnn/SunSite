package sunnn.sunsite.service.impl;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sunnn.sunsite.dao.PictureDao;
import sunnn.sunsite.dto.FileCache;
import sunnn.sunsite.dto.StatusCode;
import sunnn.sunsite.dto.request.PicInfo;
import sunnn.sunsite.service.GalleryService;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.channels.FileChannel;

@Service
public class GalleryServiceImpl implements GalleryService {

    @Autowired
    private PictureDao pictureDao;

    @Autowired
    private FileCache fileCache;

    @Override
    public StatusCode checkFile(MultipartFile file, HttpSession session) {
        if(file == null || file.isEmpty())
            return StatusCode.EMPTY_UPLOAD;
        if(pictureDao.findOne(file.getOriginalFilename()) != null)
            return StatusCode.DUPLICATED_FILENAME;
        //TODO 把图片放入缓存
        File f = new File("F:\\" + file.getOriginalFilename());
        try {
            file.transferTo(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileCache.setFile(session.getId(), f);

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
        File file = fileCache.getFile(session.getId());

        try {
            FileChannel inputChannel = new FileInputStream(file).getChannel();
            FileChannel outputChannel = new FileOutputStream(new File("D:\\" + file.getName()))
                    .getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
