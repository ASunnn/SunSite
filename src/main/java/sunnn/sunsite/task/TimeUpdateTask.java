package sunnn.sunsite.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import sunnn.sunsite.dao.CollectionDao;
import sunnn.sunsite.dao.GroupDao;
import sunnn.sunsite.dao.TypeDao;
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
    private CollectionDao collectionDao;

    @Resource
    private GroupDao groupDao;

    @Resource
    private TypeDao typeDao;

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
            Collection c = collectionDao.find(cId);

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            groupDao.update(timestamp, c.getGroup());
            collectionDao.update(timestamp, cId);
            typeDao.update(timestamp, c.getType());
        }
    }
}
