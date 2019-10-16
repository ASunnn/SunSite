package sunnn.sunsite.task;

import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import sunnn.sunsite.util.SunsiteConstant;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ThumbnailTask {

    private static Logger log = LoggerFactory.getLogger(ThumbnailTask.class);

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public void submit(String path, String name, String thumbnailName) {
        Task t = new Task(path, name, thumbnailName);

        executor.submit(t);
    }

    private class Task implements Runnable {

        private String path;

        private String name;

        private String thumbnailName;

        Task(String path, String name, String thumbnailName) {
            this.path = path;
            this.name = name;
            this.thumbnailName = thumbnailName;
        }

        @Override
        public void run() {
            String src = path + name;
            String dest = path + thumbnailName;

            try {
                Thumbnails.Builder b = Thumbnails.of(src);
                b = b.size(SunsiteConstant.THUMBNAIL_SIZE, SunsiteConstant.THUMBNAIL_SIZE);
                b.toFile(dest);
            } catch (Exception e) {
                log.error("Cannot Create Thumbnail - "+ e);
            }
        }
    }
}
