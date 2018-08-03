package sunnn.sunsite.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sunsite")
@PropertySource("file:${properties-file.path}")
public class SunSiteConstant {

    public static String pictureTempPath;

    public static String picturePath;

    public static int thumbnailSize;

    public static String pathSeparator;

    public void setPictureTempPath(String pictureTempPath) {
        SunSiteConstant.pictureTempPath = pictureTempPath;
    }

    public void setPicturePath(String picturePath) {
        SunSiteConstant.picturePath = picturePath;
    }

    public static void setThumbnailSize(int thumbnailSize) {
        SunSiteConstant.thumbnailSize = thumbnailSize;
    }

    public static void setPathSeparator(String pathSeparator) {
        SunSiteConstant.pathSeparator = pathSeparator;
    }
}
