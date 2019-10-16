package sunnn.sunsite;

import com.github.kilianB.hash.Hash;
import com.github.kilianB.hashAlgorithms.DifferenceHash;
import com.github.kilianB.matcher.persistent.database.DatabaseImageMatcher;
import com.github.kilianB.matcher.persistent.database.H2DatabaseImageMatcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import sunnn.sunsite.dao.*;
import sunnn.sunsite.exception.UnSupportSystemException;
import sunnn.sunsite.service.PictureService;
import sunnn.sunsite.task.ThumbnailTask;
import sunnn.sunsite.util.MD5s;
import sunnn.sunsite.util.SunSiteProperties;
import sunnn.sunsite.util.SunsiteConstant;
import sunnn.sunsite.util.Utils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.*;

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
		String databasePath = null;
		try {
			databasePath = Utils.getPropertiesPath() + SunsiteConstant.SEARCH_BY_IMAGE_DATABASE;
		} catch (UnSupportSystemException ignored) {
		}

		String url = "jdbc:h2:" + databasePath;
		Class.forName("org.h2.Driver");

		Connection conn = DriverManager.getConnection(url, "root", "");

		try (Statement stmt = conn.createStatement()) {
			ResultSet rs = stmt.executeQuery("SELECT * FROM DifferenceHashm2145378454");
			if (rs.next()) {
				String seq = rs.getString(1);
				byte[] image = rs.getBytes(2);

				// TODO 输出
			}
		}

//		DatabaseImageMatcher matcher = null;
//		try {
//			matcher = getImageMatcher();
//		} catch (SQLException | ClassNotFoundException e) {
//		}
//		matcher = setAlgorithm(matcher);
//
//		matcher.addImage("TESTTEST",
//				new File("C:\\Users\\劉日豐\\Desktop\\76563727_p0.png"));
	}

	private DatabaseImageMatcher getImageMatcher() throws SQLException, ClassNotFoundException {
		String databasePath = null;
		try {
			databasePath = Utils.getPropertiesPath() + SunsiteConstant.SEARCH_BY_IMAGE_DATABASE;
		} catch (UnSupportSystemException ignored) {
		}

		String url = "jdbc:h2:" + databasePath;
		Class.forName("org.h2.Driver");

		Connection conn = DriverManager.getConnection(url, "root", "");
		return new H2DatabaseImageMatcher(conn);
	}

	private DatabaseImageMatcher setAlgorithm(DatabaseImageMatcher matcher) {
		matcher.addHashingAlgorithm(new DifferenceHash(64, DifferenceHash.Precision.Double), .2);
		return matcher;
	}
}
