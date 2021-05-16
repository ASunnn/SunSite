package sunnn.sunsite.recover;

import cn.hutool.core.io.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sunnn.sunsite.dao.CollectionDao;
import sunnn.sunsite.dao.GroupDao;
import sunnn.sunsite.dao.IllustratorDao;
import sunnn.sunsite.dao.PicDao;
import sunnn.sunsite.dto.CollectionInfo;
import sunnn.sunsite.dto.GroupInfo;
import sunnn.sunsite.dto.IllustratorInfo;
import sunnn.sunsite.entity.Pic;
import sunnn.sunsite.service.CollectionService;
import sunnn.sunsite.service.GroupService;
import sunnn.sunsite.service.IllustratorService;
import sunnn.sunsite.service.PictureService;
import sunnn.sunsite.util.StatusCode;
import sunnn.sunsite.util.SunSiteProperties;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

@Service
public class DeleteRecover {

    private final PictureService pictureService;

    private final IllustratorService illustratorService;

    private final CollectionService collectionService;

    private final GroupService groupService;

    @Resource
    private PicDao picDao;

    @Resource
    IllustratorDao illustratorDao;

    @Resource
    CollectionDao collectionDao;

    @Resource
    GroupDao groupDao;

    @Autowired
    public DeleteRecover(PictureService pictureService, IllustratorService illustratorService, CollectionService collectionService, GroupService groupService) {
        this.pictureService = pictureService;
        this.illustratorService = illustratorService;
        this.collectionService = collectionService;
        this.groupService = groupService;
    }

    public void removePic() {
        int miss = 0;
        int failDelete = 0;

        int count = picDao.count();
        int sign = 0;
        while (sign <= count) {
            // 100个循环
            List<Pic> picList = picDao.findAll(sign, 100);
            System.out.println("filter pics " + sign);
            sign += 100;

            for (Pic p : picList) {
                String path = SunSiteProperties.savePath + p.getPath() + p.getName();
                if (!FileUtil.exist(path)) {
                    // 如果有已经删除的
                    System.out.println("cannot find - " + path);
                    ++miss;
                    if (pictureService.delete(p.getSequence()) != StatusCode.OJBK) {
                        // 数据库删除错误计数报错
                        System.err.println("cannot del - " + path);
                        ++failDelete;
                    }
                }
            }
        }

        System.out.println(miss + "miss   " + failDelete + "fail");
    }

    public void removeIll() {
        int zero = 0;
        int failDelete = 0;

        List<IllustratorInfo> ills = illustratorDao.findAllInfoByName("", 0, 600);
        System.out.println("filter ills " + ills.size());

        for (IllustratorInfo i : ills) {
            if (i.getPost() == 0) {
                // 如果有已经删除的
                ++zero;
                if (illustratorService.delete(i.getIllustrator()) != StatusCode.OJBK) {
                    // 数据库删除错误计数报错
                    System.err.println("cannot del - " + i.getIllustrator());
                    ++failDelete;
                }
            }
        }

        System.out.println(zero + "miss   " + failDelete + "fail");
    }

    public void removeCollection() {
        int miss = 0;
        int failDelete = 0;

        List<CollectionInfo> collectionInfos = collectionDao.findAllInfo(0, 800);
        System.out.println("filter col " + collectionInfos.size());

        for (CollectionInfo c : collectionInfos) {
            String parentPath = SunSiteProperties.savePath + c.getType() + File.separator + c.getGroup();
            String path = parentPath + File.separator + c.getCollection();
            if (!FileUtil.exist(path)) {
                // 如果有已经删除的
                System.out.println("cannot find - " + path);
                ++miss;
                if (collectionService.delete(c.getSequence()) != StatusCode.OJBK) {
                    // 数据库删除错误计数报错
                    System.err.println("cannot del - " + path);
                    ++failDelete;
                }
            }
        }

        System.out.println(miss + "miss   " + failDelete + "fail");
    }

    public void removeGroup() {
        int miss = 0;
        int failDelete = 0;

        List<GroupInfo> groupInfos = groupDao.findAllInfo(0, 800);
        System.out.println("filter group " + groupInfos.size());

        for (GroupInfo g : groupInfos) {
            if (g.getBook() == 0 && g.getPost() == 0) {
                // 如果有已经删除的
                System.out.println("cannot find - " + g.getGroup());
                ++miss;
                if (groupService.delete(g.getGroup()) != StatusCode.OJBK) {
                    // 数据库删除错误计数报错
                    System.err.println("cannot del - " + g.getGroup());
                    ++failDelete;
                }
            }
        }

        System.out.println(miss + "miss   " + failDelete + "fail");
    }
}
