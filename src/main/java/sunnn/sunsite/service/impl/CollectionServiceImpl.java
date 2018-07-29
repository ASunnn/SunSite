package sunnn.sunsite.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sunnn.sunsite.dao.CollectionDao;
import sunnn.sunsite.entity.Collection;
import sunnn.sunsite.service.PictureInfoService;

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
}
