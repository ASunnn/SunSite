package sunnn.sunsite;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import sunnn.sunsite.dao.*;
import sunnn.sunsite.dto.IllustratorInfo;
import sunnn.sunsite.service.CollectionService;
import sunnn.sunsite.task.ThumbnailTask;
import sunnn.sunsite.util.FileUtils;
import sunnn.sunsite.util.InputDataScanner;
import sunnn.sunsite.util.SunSiteProperties;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SunSiteApplicationTests {

	@Autowired
	private CollectionService service;

	@Autowired
	private ThumbnailTask task;

	@Resource
	private IllustratorMapper mapper;

	@Autowired
	private InputDataScanner scanner;

	@Test
	public void contextLoads() throws InterruptedException, IOException {
		scanner.scan(SunSiteProperties.scanAutoFill);
	}
}
