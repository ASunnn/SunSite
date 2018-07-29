package sunnn.sunsite.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sunnn.sunsite.dao.IllustratorDao;
import sunnn.sunsite.entity.Illustrator;
import sunnn.sunsite.service.PictureInfoService;

import java.util.List;

@Service
public class IllustratorServiceImpl implements PictureInfoService {

    private final IllustratorDao illustratorDao;

    @Autowired
    public IllustratorServiceImpl(IllustratorDao illustratorDao) {
        this.illustratorDao = illustratorDao;
    }

    @Override
    public List<Illustrator> getList() {
        return illustratorDao.getAllIllustrator();
    }
}
