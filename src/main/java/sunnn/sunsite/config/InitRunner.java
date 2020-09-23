package sunnn.sunsite.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import sunnn.sunsite.dao.*;
import sunnn.sunsite.util.*;

import javax.annotation.Resource;

/**
 * https://docs.spring.io/spring-boot/docs/2.0.7.RELEASE/reference/htmlsingle/#boot-features-command-line-runner
 */
@Component
public class InitRunner implements ApplicationRunner {

    private static Logger log = LoggerFactory.getLogger(InitRunner.class);

    private static final int[] versions = {200, 201, 203, 210, 220};

    @Resource
    private SysDao sysDao;

    @Override
    public void run(ApplicationArguments args) {
        deleteTempPath();

        checkPatch();
    }

    private void deleteTempPath() {
        if (!FileUtils.deletePathForce(SunSiteProperties.tempPath))
            log.warn("Clean TempPath Fail In InitializeRunner. Maybe TempPath Not Exists");
    }

    private void checkPatch() {
        int currentVersion = sysDao.selectVersion();

        // 若sys表为空，需要创建记录
        if (currentVersion == -1) {
            currentVersion = 0;
            sysDao.insert(0, "");
        }

        int patchVersion = currentVersion;

        try {
            for (int version : versions) {
                if (currentVersion < version) {
                    new Patch().patchInvoker(version);
                    patchVersion = version;
                }
            }
        } catch (Exception e) {
            log.error("Error Occurred While Executing The Patch : ", e);
        } finally {
            sysDao.updateVersion(currentVersion, patchVersion);
        }
    }

    class Patch {

        void patchInvoker(int version) {
            switch (version) {
                case 200:
                    // 由于代码重构，不再兼容200版本补丁
                    patch200();
                    break;
            }
        }

        @Deprecated
        private void patch200() {
//            String collectionMsg = collectionReview();
//            String pictureMsg = pictureReview();
//
//            // 处理推送消息
//            StringBuilder msg = new StringBuilder();
//            if (!collectionMsg.isEmpty())
//                msg.append("Naming Collection conflict : ")
//                        .append(collectionMsg);
//            if (!pictureMsg.isEmpty())
//                msg.append("  Naming Picture conflict : ")
//                        .append(pictureMsg);
//            if (msg.length() == 0)
//                msg.append("Update Sequence Complete");
//            messageBoxService.pushMessage(msg.toString());
        }

//        private String collectionReview() {
//            StringBuilder collectionMsg = new StringBuilder();
//
//            int count = collectionDao.count();
//            int pageCount = (int) Math.ceil((double) count / SunsiteConstant.PAGE_SIZE);
//
//            for (int page = 0; page < pageCount; ++page) {
//                List<CollectionInfo> collectionInfos =
//                        collectionDao.findAllInfo(page * SunsiteConstant.PAGE_SIZE, SunsiteConstant.PAGE_SIZE);
//
//                for (CollectionInfo cInfo : collectionInfos) {
//                    // 计算新cId
//                    String md5Source = cInfo.getGroup() + cInfo.getCollection();
//                    long newCId = MD5s.getMD5Sequence(md5Source);
//
//                    if (newCId == cInfo.getSequence())
//                        continue;
//
//                    if (collectionDao.find(newCId) != null) { // 有冲突
//                        int renameCount = 1;
//                        int r;
//                        do {
//                            String newName = cInfo.getCollection() + "-" + (renameCount++);
//                            ModifyResultResponse res = collectionService.modifyName(cInfo.getSequence(), newName);
//                            r = res.getCode();
//                        } while (r != StatusCode.OJBK.getCode());
//
//                        collectionMsg.append(" [")
//                                .append(cInfo.getGroup())
//                                .append(" - ")
//                                .append(cInfo.getCollection())
//                                .append("] ");
//                        continue;
//                    }
//                    // cId不同需要更新
//                    collectionDao.updateCId(cInfo.getSequence(), newCId);
//                    // 更新关联图片
//                    List<Picture> pictureList = pictureDao.findAllByCollection(cInfo.getSequence());
//                    for (Picture picture : pictureList) {
//                        // 直接使用已有接口
//                        pictureDao.updateCollection(picture.getSequence(), picture.getSequence(), newCId);
//                    }
//                }
//            }
//            return collectionMsg.toString();
//        }

//        private String pictureReview() {
//            StringBuilder pictureMsg = new StringBuilder();
//
//            int count = picDao.count();
//            int pageCount = (int) Math.ceil((double) count / SunsiteConstant.PAGE_SIZE);
//
//            for (int page = 0; page < pageCount; ++page) {
//                List<Pic> pictures =
//                        picDao.findAll(page * SunsiteConstant.PAGE_SIZE, SunsiteConstant.PAGE_SIZE);
//
//                for (Pic p : pictures) {
//                    PictureBase pBase = pictureDao.findBaseInfo(p.getSequence());
//
//                    String md5Source = pBase.getGroup() + pBase.getCollection() + pBase.getName();
//                    long newSequence = MD5s.getMD5Sequence(md5Source);
//
//                    if (newSequence == pBase.getSequence())
//                        continue;
//
//                    if (picDao.find(newSequence) != null) {
//                        int renameCount = 1;
//                        int r;
//                        do {
//                            String newName = (renameCount++) + "-" + p.getName();
//                            ModifyResultResponse res = pictureService.modifyPicture(p.getSequence(), newName);
//                            r = res.getCode();
//                        } while (r != StatusCode.OJBK.getCode());
//
//                        pictureMsg.append(" [")
//                                .append(pBase.getGroup())
//                                .append(" - ")
//                                .append(pBase.getCollection())
//                                .append(" - ")
//                                .append(p.getName())
//                                .append("] ");
//                        continue;
//                    }
//                    // 直接使用已有接口来更新序列号
//                    picDao.updateName(p.getSequence(), newSequence, p.getName(), p.getThumbnailName());
//                    pictureDao.updateCollection(pBase.getSequence(), newSequence, pBase.getCId());
//                    // 更新artwork表的序列号关联
//                    illustratorDao.updatePicture(pBase.getSequence(), newSequence);
//                }
//            }
//            return pictureMsg.toString();
//        }
    }
}


