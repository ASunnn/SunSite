package sunnn.sunsite.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import sunnn.sunsite.dao.CollectionMapper;
import sunnn.sunsite.dao.GroupMapper;
import sunnn.sunsite.dao.TypeMapper;
import sunnn.sunsite.entity.Collection;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class TimeUpdateTask {

    private static Logger log = LoggerFactory.getLogger(ThumbnailTask.class);

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Resource
    private CollectionMapper collectionMapper;

    @Resource
    private GroupMapper groupMapper;

    @Resource
    private TypeMapper typeMapper;

    public void submit(long collection) {
        Task t = new Task(collection);

        executor.submit(t);
    }

    private class Task implements Runnable {

        private long cId;

        public Task(long collection) {
            this.cId = collection;
        }

        @Override
        public void run() {
            Collection c = collectionMapper.find(cId);

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            groupMapper.update(timestamp, c.getGroup());
            collectionMapper.update(timestamp, cId);
            typeMapper.update(timestamp, c.getType());
        }
    }
}
