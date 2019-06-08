package sunnn.sunsite;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import sunnn.sunsite.dao.*;
import sunnn.sunsite.service.PictureService;
import sunnn.sunsite.task.ThumbnailTask;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SunSiteApplicationTests {

	@Autowired
	private PictureService service;

	@Autowired
	private ThumbnailTask task;

	@Resource
	private PictureDao dao;

	@Test
	public void contextLoads() throws Exception {
	}
}
