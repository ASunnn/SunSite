package sunnn.sunsite.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sunnn.sunsite.util.FileCache;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class CacheClearer {

    private static Logger log = LoggerFactory.getLogger(CacheClearer.class);

    private final FileCache fileCache;

    @Autowired
    public CacheClearer(FileCache fileCache) {
        this.fileCache = fileCache;
    }

    /**
     * 每小时执行一次缓存清理任务
     */
    @Scheduled(cron = "0 * * * * *")
    public void clearFileCache() {
        log.info("Run Scheduled Task : " +
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()));
        log.info("Before Clear : " + String.valueOf(fileCache.getSize()));
        fileCache.clearCache();
        log.info("After Clear : " + String.valueOf(fileCache.getSize()));
    }

}
