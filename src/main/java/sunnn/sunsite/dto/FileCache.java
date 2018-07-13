package sunnn.sunsite.dto;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

public class FileCache {

    private ConcurrentHashMap<String, TempFile> cache;

    private int expiration;

    public FileCache() {
        this(4, 1800);
    }

    public FileCache(int initCapacity, int expiration) {
        cache = new ConcurrentHashMap<>(initCapacity);
        this.expiration = expiration;
    }

    public void setFile(String key, File file) {
        cache.put(key,
                new TempFile(file, now()));
    }

    public File getFile(String key) {
        return cache.get(key).getFile();
    }

    private long now() {
        return System.currentTimeMillis();
    }

    private class TempFile {

        private File file;

        private long receiveTime;

        TempFile(File file, long receiveTime) {
            this.file = file;
            this.receiveTime = receiveTime;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public long getReceiveTime() {
            return receiveTime;
        }

        public void setReceiveTime(long receiveTime) {
            this.receiveTime = receiveTime;
        }
    }

}

