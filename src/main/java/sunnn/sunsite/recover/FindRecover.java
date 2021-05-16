package sunnn.sunsite.recover;

import cn.hutool.core.io.FileUtil;
import org.springframework.stereotype.Service;
import sunnn.sunsite.dao.PicDao;
import sunnn.sunsite.dao.RecoverDao;
import sunnn.sunsite.util.MD5s;
import sunnn.sunsite.util.SunSiteProperties;

import javax.annotation.Resource;
import java.io.File;
import java.sql.Timestamp;

@Service
public class FindRecover {

    @Resource
    private RecoverDao recoverDao;

    @Resource
    private PicDao picDao;

    public void listFile(String dir) {
        String type = dir;
        String group;
        String collection;
        int count = 0;

        File[] groupDs = FileUtil.ls(SunSiteProperties.savePath + dir);

        for (File groupDir: groupDs) {
            File[] colDs = FileUtil.ls(groupDir.getAbsolutePath());
            group = groupDir.getName();
            for (File colDir: colDs) {
                File[] pics = FileUtil.ls(colDir.getAbsolutePath());
                collection = colDir.getName();
                for (File pic : pics) {
                    if (pic.getName().startsWith("thumbnail_")) continue;

                    String md5Source = group + collection + pic.getName();
                    long sequence = MD5s.getMD5Sequence(md5Source);
                    if (picDao.find(sequence) != null)  continue;

                    System.out.println(pic.getAbsolutePath());
                    ++count;

                    String path = type + File.separator + group + File.separator + collection + File.separator;
                    Timestamp uploadTime = new Timestamp(FileUtil.lastModifiedTime(pic).getTime());
                    long cId = MD5s.getMD5Sequence(group + collection);

                    Recover recover = new Recover().setSeq(sequence).setName(pic.getName()).setPath(path).setType(type).setGroup(group).setCollection(collection).setUploadTime(uploadTime).setCId(cId);
                    recoverDao.insert(recover);
                }
            }
        }
        System.out.println(count);
    }

    public int countFile(String dir) {
        int count = 0;

        File[] groupDs = FileUtil.ls(SunSiteProperties.savePath + dir);

        for (File groupDir: groupDs) {
            File[] colDs = FileUtil.ls(groupDir.getAbsolutePath());
            for (File colDir: colDs) {
//                int countG = 0;
                File[] pics = FileUtil.ls(colDir.getAbsolutePath());
                for (File pic : pics) {
                    if (pic.getName().startsWith("thumbnail_")) continue;
                    ++count;
//                    ++countG;
                }
//                System.out.println(countG + " in " + colDir.getAbsolutePath());
            }
        }
        return count;
    }
}
