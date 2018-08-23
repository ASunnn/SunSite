package sunnn.sunsite.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import sunnn.sunsite.util.FileUtils;
import sunnn.sunsite.util.SunSiteConstant;

@Component
public class InitRunner implements ApplicationRunner {

    private static Logger log = LoggerFactory.getLogger(InitRunner.class);

    @Override
    public void run(ApplicationArguments args) {
        if (!FileUtils.deletePathForce(SunSiteConstant.tempPath))
            log.warn("Clean TempPath Fail In InitializeRunner. Maybe TempPath Not Exists");
    }
}
