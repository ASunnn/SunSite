package sunnn.sunsite.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sunsite")
@PropertySource("file:${properties-file.path}")
public class SunSiteConstant {

    public static String pictureTempPath;

    public static String picturePath;

    public String getPictureTempPath() {
        return pictureTempPath;
    }

    public void setPictureTempPath(String pictureTempPath) {
        SunSiteConstant.pictureTempPath = pictureTempPath;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        SunSiteConstant.picturePath = picturePath;
    }
}
