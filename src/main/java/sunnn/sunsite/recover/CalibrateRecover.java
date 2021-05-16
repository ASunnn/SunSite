package sunnn.sunsite.recover;

import cn.hutool.core.io.FileUtil;
import org.springframework.stereotype.Service;
import sunnn.sunsite.dao.PicDao;
import sunnn.sunsite.dao.RecoverDao;
import sunnn.sunsite.entity.Pic;
import sunnn.sunsite.util.StatusCode;
import sunnn.sunsite.util.SunSiteProperties;
import sunnn.sunsite.util.Utils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CalibrateRecover {

    @Resource
    private PicDao picDao;

    @Resource
    private RecoverDao recoverDao;

    public void calibrateInfo() {
        int diff = 0;

        int count = picDao.count();
        int sign = 0;
        while (sign <= count) {
            // 1000个循环
            List<Pic> picList = picDao.findAll(sign, 1000);
            System.out.println("filter pics " + sign);
            sign += 1000;

            for (Pic p : picList) {
                String path = SunSiteProperties.savePath + p.getPath() + p.getName();
                if (!FileUtil.exist(path)) {
                    // 如果有已经删除的
                    System.err.println("cannot find - " + path);
                }
                Pic curr = generateInfo(new File(path), p);
                if (curr == null) System.err.println("cannot gene - " + path);
                if (curr.getSize() == p.getSize()
                        && curr.getHeight() == p.getHeight()
                        && curr.getWidth() == p.getWidth()) {
                    continue;
                }

                System.out.println(path);
//                recoverDao.update(p.getSequence(), curr.getSize(), curr.getHeight(), curr.getWidth());
                diff++;
            }
        }

        System.out.println(diff + "diff");
    }

    private Pic generateInfo(File file, Pic p) {
        Pic picture = new Pic();

        picture.setSequence(p.getSequence())
                .setName(file.getName())
                .setSize(file.length())
                .setUploadTime(p.getUploadTime())
                .setPath(p.getPath())
                .setThumbnailName(p.getThumbnailName());

        // get图片长宽
        try {
            int[] pictureSize = Utils.getPictureSize(file.getPath());
            picture.setWidth(pictureSize[0]);
            picture.setHeight(pictureSize[1]);
        } catch (IOException e) {
            return null;
        }

        if (picture.getWidth() < picture.getHeight())
            picture.setVOrH(-1);
        else if (picture.getWidth() > picture.getHeight())
            picture.setVOrH(1);
        else
            picture.setVOrH(0);

        return picture;
    }
}
