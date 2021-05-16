package sunnn.sunsite;

import cn.hutool.core.io.FileUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import sunnn.sunsite.recover.*;
import sunnn.sunsite.util.MD5s;

import java.io.File;
import java.sql.Timestamp;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SunSiteApplicationTests {

	@Autowired
	private DeleteRecover deleteRecover;

	@Autowired
	private FindRecover findRecover;

	@Autowired
	private BuildRecover buildRecover;

	@Autowired
	private CalibrateRecover calibrateRecover;

	@Test
	public void contextLoads() throws Exception {
	}
}
