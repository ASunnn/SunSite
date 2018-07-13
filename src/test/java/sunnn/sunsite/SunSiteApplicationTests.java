package sunnn.sunsite;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import sunnn.sunsite.dao.CollectionDao;
import sunnn.sunsite.dao.PictureDao;
import sunnn.sunsite.dao.IllustratorDao;
import sunnn.sunsite.dao.TypeDao;
import sunnn.sunsite.entity.Collection;
import sunnn.sunsite.entity.Picture;
import sunnn.sunsite.entity.Illustrator;
import sunnn.sunsite.entity.Type;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SunSiteApplicationTests {

	@Autowired
	private PictureDao pictureDao;

	@Autowired
	private CollectionDao collectionDao;

	@Autowired
	private TypeDao typeDao;

	@Autowired
	private IllustratorDao illustratorDao;

	@Test
	public void insertPicture() {
		Type type = new Type("illustration");
		typeDao.insert(type);

		Illustrator illustrator = new Illustrator("しらたま");
		illustratorDao.insert(illustrator);

		Collection collection = new Collection("Other", type);
		collectionDao.insert(collection);

		Picture picture = new Picture();
		picture.setIllustrator(illustrator);
		picture.setCollection(collection);
		picture.setPath("/usr/sunsite/");
		picture.setFileName("0000000.png");
		picture.setUploadTime(System.currentTimeMillis());
		pictureDao.insert(picture);
	}


	@Test
	public void contextLoads() {
	}

}
