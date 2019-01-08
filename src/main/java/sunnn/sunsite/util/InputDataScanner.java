package sunnn.sunsite.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import sunnn.sunsite.dto.CollectionBase;
import sunnn.sunsite.dto.request.UploadPictureInfo;
import sunnn.sunsite.service.CollectionService;
import sunnn.sunsite.service.PictureService;

import java.io.File;

public class InputDataScanner {

    private static Logger log = LoggerFactory.getLogger(InputDataScanner.class);

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private PictureService pictureService;

    @Autowired
    private FileCache fileCache;

    private boolean autoFill = false;

    public void scan(boolean autoFill) {
        scan(SunSiteProperties.tempPath, autoFill);
    }

    public void scan(String path, boolean autoFill) {
        this.autoFill = autoFill;

        File target = new File(path);

        if (target.exists() && target.isDirectory())
            typeScanner(target);
    }

    private void typeScanner(File basePath) {
        File[] targets = basePath.listFiles();

        for (File target : targets != null ? targets : new File[0]) {
            if (target.isDirectory()) {
                String typeName = target.getName();

                groupScanner(target, typeName);
            }
        }
    }

    private void groupScanner(File basePath, String type) {
        File[] targets = basePath.listFiles();

        for (File target : targets != null ? targets : new File[0]) {
            if (target.isDirectory()) {
                String groupName = target.getName();

                collectionScanner(target, type, groupName);
            }
        }
    }

    private void collectionScanner(File basePath, String type, String group) {
        File[] targets = basePath.listFiles();

        for (File target : targets != null ? targets : new File[0]) {
            if (target.isDirectory()) {
                String collectionName = target.getName();

                pictureScanner(target, type, group, collectionName);
            }
        }
    }

    private void pictureScanner(File basePath, String type, String group, String collection) {
        File[] targets = basePath.listFiles(
                pathname -> pathname.isFile() && !FileUtils.fileNameMatcher(pathname.getName()));

        registerData(type, group, collection, targets);
    }

    private void registerData(String type, String group, String collection, File[] targets) {
        if (targets == null || targets.length <= 0)
            return;

        if (prepareRegister(type, group, collection)) {
            StatusCode code = pictureServiceAdapter(type, group, collection, targets);

            if (code.equals(StatusCode.OJBK))
                FileUtils.deletePathForce(targets[0].getParentFile());
            else
                log.warn("Service Return '" + code.toString() + "' While Scan The Path Of "
                        + type + "-" + group + "-" + collection);
        }
    }

    private StatusCode pictureServiceAdapter(String type, String group, String collection, File[] pics) {
        UploadPictureInfo info = (UploadPictureInfo) generatePictureInfo(group, collection);

        for (File p : pics)
            fileCache.setFile(info.getUploadCode(), p);

        StatusCode code = pictureService.uploadInfoAndSave(info);

        fileCache.remove(info.getUploadCode());

        return code;
    }

    private boolean prepareRegister(String type, String group, String collection) {
        CollectionBase collectionInfo = new CollectionBase()
                .setType(type)
                .setGroup(group)
                .setCollection(collection);
        StatusCode code = collectionService.createCollection(collectionInfo);

        return code.equals(StatusCode.OJBK) || code.equals(StatusCode.DUPLICATE_INPUT);
    }

    private Object generatePictureInfo(String group, String collection) {
        String uploadCode = String.valueOf(System.currentTimeMillis())
                + group + collection;

        return new UploadPictureInfo()
                .setGroup(group)
                .setCollection(collection)
                .setIllustrator(autoFill ? group : "")
                .setUploadCode(uploadCode);
    }
}
