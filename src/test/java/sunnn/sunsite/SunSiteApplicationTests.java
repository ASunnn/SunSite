package sunnn.sunsite;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import sunnn.sunsite.dao.*;
import sunnn.sunsite.dto.CollectionBase;
import sunnn.sunsite.dto.PictureBase;
import sunnn.sunsite.service.CollectionService;
import sunnn.sunsite.service.MessageBoxService;
import sunnn.sunsite.service.PictureService;
import sunnn.sunsite.task.ThumbnailTask;
import sunnn.sunsite.util.MD5s;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SunSiteApplicationTests {

	@Autowired
	private PictureService service;

	@Autowired
	private ThumbnailTask task;

	@Resource
	private CollectionDao collectionDao;

	@Resource
	private PictureDao dao;

	@Test
	public void contextLoads() throws Exception {
		PictureBase pBase = dao.findBaseInfo(3236811410305410769L);
		System.out.println(pBase.toString());

		CollectionBase info = collectionDao.findBaseInfo(pBase.getCId());

		String md5Source = info.getGroup() + info.getCollection() + pBase.getName();
		long newSequence = MD5s.getMD5Sequence(md5Source);
		System.out.println(newSequence);
	}
}
