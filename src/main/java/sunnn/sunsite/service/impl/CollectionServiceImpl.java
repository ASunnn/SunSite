package sunnn.sunsite.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sunnn.sunsite.dao.CollectionDao;
import sunnn.sunsite.entity.Collection;
import sunnn.sunsite.service.PictureInfoService;
import sunnn.sunsite.util.StatusCode;

import java.io.File;
import java.util.List;

@Service
public class CollectionServiceImpl implements PictureInfoService {

    private final CollectionDao collectionDao;

    @Autowired
    public CollectionServiceImpl(CollectionDao collectionDao) {
        this.collectionDao = collectionDao;
    }

    @Override
    public List<Collection> getList() {
        return collectionDao.getAllCollection();
    }

    @Override
    public List getRelatedList(String name) {
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
}
