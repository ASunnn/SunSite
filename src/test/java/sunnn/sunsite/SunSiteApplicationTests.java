package sunnn.sunsite;

import net.coobird.thumbnailator.Thumbnails;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import sunnn.sunsite.dao.*;
import sunnn.sunsite.dto.IllustratorInfo;
import sunnn.sunsite.entity.Pic;
import sunnn.sunsite.service.CollectionService;
import sunnn.sunsite.task.ThumbnailTask;
import sunnn.sunsite.util.FileUtils;
import sunnn.sunsite.util.InputDataScanner;
import sunnn.sunsite.util.SunSiteProperties;
import sunnn.sunsite.util.SunsiteConstant;

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
		task.submit("D:\\", "yande.re 351311 cleavage dress koi_kakeru_shin-ai_kanojo komari_yui kunimi_nako no_bra see_through shiratama summer_dress us-track.png",
				Pic.THUMBNAIL_PREFIX + "yande.re 351311 cleavage dress koi_kakeru_shin-ai_kanojo komari_yui kunimi_nako no_bra see_through shiratama summer_dress us-track.jpg");
		task.submit("D:\\", "yande.re 351312 cameltoe cleavage dress koi_kakeru_shin-ai_kanojo kurasawa_moko maid pantsu shijou_rinka shindou_ayane stockings thighhighs us-track waitress.png",
				Pic.THUMBNAIL_PREFIX + "yande.re 351312 cameltoe cleavage dress koi_kakeru_shin-ai_kanojo kurasawa_moko maid pantsu shijou_rinka shindou_ayane stockings thighhighs us-track waitress.jpg");
		Thread.sleep(100000000);
//		try {
//			Thumbnails.of("D:\\yande.re 351311 cleavage dress koi_kakeru_shin-ai_kanojo komari_yui kunimi_nako no_bra see_through shiratama summer_dress us-track.png")
//					.size(SunsiteConstant.thumbnailSize, SunsiteConstant.thumbnailSize)
//					.toFile("D:\\test.jpg");
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
	}
}
