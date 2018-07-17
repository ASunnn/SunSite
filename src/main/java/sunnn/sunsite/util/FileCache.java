package sunnn.sunsite.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import sunnn.sunsite.config.SunSiteConstant;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 上传文件的缓存类
 */
public class FileCache {

    private static Logger log = LoggerFactory.getLogger(FileCache.class);

    private ConcurrentHashMap<String, TempFiles> cache;

    /**
     * 缓存过期时间
     * <p>
     * 为了防止数据库业务发生事务性问题，建议缓存过期时间大于session的超时时间
     */
    private long expiration;

    public FileCache() {
        this(16, 120000);
    }

    public FileCache(int initCapacity, int expiration) {
        cache = new ConcurrentHashMap<>(initCapacity);
        this.expiration = expiration;
    }

    /**
     * 设置缓存
     *
     * @param key  键
     * @param file 文件
     * @throws IOException 文件保存失败
     */
    public void setFile(String key, MultipartFile file) throws IOException {
        /*
            检查是否已经有相同的键
         */
        if (!cache.containsKey(key))
            cache.put(key,  //若是新缓存，则新建一个缓存
                    new TempFiles(now()));
        /*
            保存文件
            已有缓存情况下新文件直接追加到里面
         */
        File f = storeFile(file);
        cache.get(key).setFiles(f);
    }

    /**
     * 获取缓存的文件
     *
     * @param key 键
     * @return 缓存内对应的所有文件
     */
    public List<File> getFile(String key) {
        return cache.get(key).getFiles();
    }

    /**
     * 清除过期缓存
     * <p>
     * 此方法由定时任务调用
     */
    public void clearCache() {
        Iterator<Map.Entry<String, TempFiles>> i = cache.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, TempFiles> r = i.next();

            long saveTime = r.getValue().getReceiveTime();
            if (now() - saveTime > expiration) {
                deleteFile(r.getValue().getFiles());
                i.remove();
            }
        }
    }

    public int getSize() {
        return cache.size();
    }

    private long now() {
        return System.currentTimeMillis();
    }

    private File storeFile(MultipartFile file) throws IOException {
        File f = new File(SunSiteConstant.pictureTempPath + file.getOriginalFilename());
        file.transferTo(f);
        return f;
    }

    private void deleteFile(List<File> files) {
        for (File file : files)
            if (!file.delete())
                log.warn("Delete Cache Failed : " + file.getName());
    }

    private class TempFiles {

        private List<File> files;

        private long receiveTime;

        TempFiles(long receiveTime) {
            this.files = new LinkedList<>();
            this.receiveTime = receiveTime;
        }

        List<File> getFiles() {
            return files;
        }

        void setFiles(File file) {
            this.files.add(file);
        }

        long getReceiveTime() {
            return receiveTime;
        }

        void setReceiveTime(long receiveTime) {
            this.receiveTime = receiveTime;
        }
    }

}

