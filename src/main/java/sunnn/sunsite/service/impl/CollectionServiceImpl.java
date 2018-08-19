package sunnn.sunsite.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sunnn.sunsite.dao.CollectionDao;
import sunnn.sunsite.dao.PictureDao;
import sunnn.sunsite.entity.Collection;
import sunnn.sunsite.entity.Picture;
import sunnn.sunsite.service.PictureInfoService;
import sunnn.sunsite.util.StatusCode;

import java.io.File;
import java.util.List;

@Service
public class CollectionServiceImpl implements PictureInfoService {

    private static Logger log = LoggerFactory.getLogger(IllustratorServiceImpl.class);

    private final CollectionDao collectionDao;

    private final PictureDao pictureDao;

    @Autowired
    public CollectionServiceImpl(CollectionDao collectionDao, PictureDao pictureDao) {
        this.collectionDao = collectionDao;
        this.pictureDao = pictureDao;
    }

    @Override
    public List<Collection> getList() {
        return collectionDao.getAllCollection();
    }

    @Override
    public List<String> getRelatedList(String name) {
        return null;
    }

    @Override
    public File download(String name) {
        return null;
    }

    @Override
    public StatusCode changeName(String oldName, String newName) {
        return null;
    }

    @Override
    public StatusCode delete(String name) {
        return null;
    }

    @Override
    public long getThumbnailSequence(String name) {
        Picture p = pictureDao.getFirst(name, "collection.name");
        if (p == null) {
            log.warn("Cannot Find Any Picture In Collection : " + name);
            return -1;
        }
        return p.getSequence();
    }
}
