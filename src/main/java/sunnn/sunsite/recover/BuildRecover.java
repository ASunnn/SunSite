package sunnn.sunsite.recover;

import cn.hutool.core.io.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sunnn.sunsite.dao.*;
import sunnn.sunsite.dto.CollectionInfo;
import sunnn.sunsite.dto.request.UploadCollectionInfo;
import sunnn.sunsite.entity.*;
import sunnn.sunsite.service.CollectionService;
import sunnn.sunsite.service.IllustratorService;
import sunnn.sunsite.service.PictureService;
import sunnn.sunsite.task.PictureIndexTask;
import sunnn.sunsite.util.*;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BuildRecover {

    @Resource
    private RecoverDao recoverDao;

    @Resource
    private PicDao picDao;

    @Resource
    private PictureDao pictureDao;

    @Resource
    private IllustratorDao illustratorDao;

    @Resource
    private GroupDao groupDao;

    @Resource
    private CollectionDao collectionDao;

    @Resource
    private TypeDao typeDao;

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private PictureService pictureService;

    @Autowired
    private IllustratorService illustratorService;

    @Autowired
    private PictureIndexTask indexTask;

    public void build() {
        List<RecoverCollection> collections = recoverDao.getCollection();

        int ok = 0;
        int not = 0;

        for (RecoverCollection c: collections) {
            StatusCode code = collectionService.createCollection(new UploadCollectionInfo().setType(c.getType()).setGroup(c.getGroup()).setCollection(c.getCollection()));
            if (code != StatusCode.OJBK) {
                System.err.println(c.getGroup() + " / " + c.getCollection() + "  result: " + code.toString());
                not++;
            } else {
                ok++;
                System.out.println(c.getGroup() + " / " + c.getCollection() + "  result: " + code.toString());
            }
        }

        System.out.println(ok + "   " + not);
    }

    public void buildPic() {
        List<RecoverCollection> collections = recoverDao.getCollection();

        for (RecoverCollection c: collections) {
            List<Recover> rs = recoverDao.getPics(c.getCId());
            System.out.println("get " + rs.size() + "p in " + c.getGroup() + "/" + c.getCollection());
            for (Recover r: rs) {
                doAdd(r);
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void doAdd(Recover recover) {
        System.out.println("recover " + recover.getPath() + recover.getName());
        List<Illustrator> illustrators = illustratorHandler(new String[]{recover.getGroup()});

        File file = FileUtil.file(SunSiteProperties.savePath + recover.getPath() + recover.getName());

        // 进行文件的保存
        Pic pictureData = generateInfo(file, recover);
        if (pictureData == null)
            return;

        picDao.insert(pictureData);

        Picture pictureInfo = new Picture()
                .setSequence(pictureData.getSequence())
                .setName(pictureData.getName())
                .setCollection(recover.getCId())
                .setIndex(Integer.MAX_VALUE);
        pictureDao.insert(pictureInfo);

        List<Artwork> artworks = new ArrayList<>();
        for (Illustrator i : illustrators) {
            artworks.add(new Artwork()
                    .setIllustrator(i.getId())
                    .setSequence(pictureData.getSequence()));
        }
        if (!artworks.isEmpty())
            illustratorDao.insertAllArtwork(artworks);

        // 设置时间戳更新任务
        Collection c = collectionDao.find(recover.getCId());
        groupDao.update(recover.getUploadTime(), c.getGroup());
        collectionDao.update(recover.getUploadTime(), recover.getCId());
        typeDao.update(recover.getUploadTime(), c.getType());

        // 设置index更新任务
        indexTask.submit(c.getCId(), pictureDao.countByCollection(c.getCId()));
    }

    private List<Illustrator> illustratorHandler(String[] illustrators) {
        if (illustrators.length <= 0)
            illustrators = new String[]{Illustrator.DEFAULT_VALUE};

        List<Illustrator> result = new ArrayList<>();
        for (String i : illustrators) {
            String iTrim = i.trim();
            if (!iTrim.isEmpty())
                result.add(illustratorService.createIllustrator(iTrim));
        }

        return result;
    }

    private Pic generateInfo(File file, Recover recover) {
        Pic picture = new Pic();

        if (picDao.find(recover.getSeq()) != null) {
            System.err.println("Duplicate Pic : " + recover.getPath() + recover.getName());
            return null;
        }

        picture.setSequence(recover.getSeq())
                .setName(file.getName())
                .setSize(file.length())
                .setUploadTime(recover.getUploadTime())
                .setPath(recover.getPath());

        //缩略图文件名
        String thumbnailName = Pic.THUMBNAIL_PREFIX + picture.getName();
        Matcher extensionNameMatcher = Pattern.compile("\\.(jpg|jpeg)$").matcher(picture.getName());
        if (!extensionNameMatcher.find())
            thumbnailName = thumbnailName.substring(0, thumbnailName.lastIndexOf('.')) + ".jpg";
        picture.setThumbnailName(thumbnailName);

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
