package sunnn.sunsite.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sunnn.sunsite.dao.TypeDao;
import sunnn.sunsite.entity.Type;
import sunnn.sunsite.service.PictureInfoService;
import sunnn.sunsite.util.StatusCode;

import java.io.File;
import java.util.List;

@Service
public class TypeServiceImpl implements PictureInfoService {

    private final TypeDao typeDao;

    @Autowired
    public TypeServiceImpl(TypeDao typeDao) {
        this.typeDao = typeDao;
    }

    @Override
    public List<Type> getList() {
        return typeDao.getAllType();
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
        return 0;
    }
}
