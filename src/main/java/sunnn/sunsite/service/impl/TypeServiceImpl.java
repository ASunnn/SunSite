package sunnn.sunsite.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sunnn.sunsite.dao.TypeDao;
import sunnn.sunsite.entity.Type;
import sunnn.sunsite.service.PictureInfoService;

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
}