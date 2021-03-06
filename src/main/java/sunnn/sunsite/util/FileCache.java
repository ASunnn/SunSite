package sunnn.sunsite.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 文件的缓存工具
 * 此工具只负责保存文件信息，并不保证文件本体的修改
 *
 * @author ASun
 */
public class FileCache {

    private static Logger log = LoggerFactory.getLogger(FileCache.class);

    private ConcurrentHashMap<String, TempFiles> cache;

    /**
     * 缓存过期时间
     * 为了防止数据库业务发生事务性问题，建议缓存过期时间大于session的超时时间
     */
    private long expiration;

    public FileCache() {
        this(120000);
    }

    public FileCache(long expiration) {
        cache = new ConcurrentHashMap<>();
        this.expiration = expiration;
    }

//    /**
//     * 设置缓存
//     *
//     * @param key  键
//     * @param file 服务器接收的临时文件
//     * @throws IOException 文件保存失败
//     */
//    public void setFile(String key, MultipartFile file) throws IOException {
//        //将MultipartFile转换为File
//        File f = FileUtils.storeFile(file,
//                SunSiteConstant.tempPath + key + SunSiteConstant.pathSeparator);
//        setFile(key, f);
//    }

    /**
     * 设置缓存
     *
     * @param key  键
     * @param file 缓存文件
     */
    public synchronized void setFile(String key, File file) {
        //检查是否已经有相同的键
        checkAndPut(key);
        cache.get(key).setFiles(file);
    }

    /**
     * 设置缓存
     *
     * @param key   键
     * @param files 缓存文件
     */
    public synchronized void setFile(String key, List<File> files) {
        //检查是否已经有相同的键
        checkAndPut(key);
        cache.get(key).setFiles(files);
    }

    /**
     * 获取缓存的文件
     *
     * @param key 键
     * @return 缓存内对应的所有文件
     */
    public synchronized List<File> getFile(String key) {
        TempFiles files = cache.get(key);
        return files == null ? null : files.getFiles();
    }

    /**
     * 移除缓存记录，但不删除缓存对应的文件
     *
     * @param key 键
     * @return 如果存在缓存记录，则将保存的文件返回
     * 如果不存在，返回null
     */
    public synchronized List<File> remove(String key) {
        TempFiles files = cache.remove(key);
        return files == null ? null : files.getFiles();
    }

    /**
     * 清除过期缓存
     * 此方法由定时任务调用
     */
    public synchronized void clearCache() {
        Iterator<Map.Entry<String, TempFiles>> i = cache.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, TempFiles> r = i.next();

            long saveTime = r.getValue().getModifiedTime();
            if (now() - saveTime > expiration) {
                //删除缓存文件
                for (File file : r.getValue().getFiles())
                    if (!FileUtils.deleteFile(file))
                        log.warn("Delete Cache Failed : " + file.getName());
                //删除目录
                if (!FileUtils.deletePath(SunSiteProperties.tempPath + r.getKey()))
                    log.warn("Can not Delete TempPath : " + SunSiteProperties.tempPath + r.getKey());
                i.remove();
            }
        }
    }

    /**
     * 获取缓存的数量
     *
     * @return 缓存数量
     */
    public int getSize() {
        return cache.size();
    }

    private void checkAndPut(String key) {
        if (!isContains(key))
            cache.put(key,  //若是新缓存，则新建一个缓存
                    new TempFiles());
    }

    public synchronized boolean isContains(String key) {
        return cache.containsKey(key);
    }

    private long now() {
        return System.currentTimeMillis();
    }


    private class TempFiles {

        private List<File> files;

        private long modifiedTime;

        TempFiles() {
            this.files = new LinkedList<>();
            this.setModifiedTime();
        }

        List<File> getFiles() {
            setModifiedTime();
            return files;
        }

        void setFiles(File file) {
            setModifiedTime();
            this.files.add(file);
        }

        void setFiles(List<File> files) {
            setModifiedTime();
            this.files.addAll(files);
        }

        long getModifiedTime() {
            return modifiedTime;
        }

        void setModifiedTime() {
            this.modifiedTime = System.currentTimeMillis();
        }
    }

}

