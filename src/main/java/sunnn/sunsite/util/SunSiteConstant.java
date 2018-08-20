package sunnn.sunsite.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sunsite")
@PropertySource("file:${properties-file.path}")
public class SunSiteConstant {

    public static String tempPath;

    public static String picturePath;

    public static int thumbnailSize;

    public static String pathSeparator;

    public static long sessionTimeout;

    public static long cacheTimeout;

    public static int cacheInitCapacity;

    public static String getTempPath() {
        return tempPath;
    }

    public static void setTempPath(String tempPath) {
        SunSiteConstant.tempPath = tempPath;
    }

    public static String getPicturePath() {
        return picturePath;
    }

    public static void setPicturePath(String picturePath) {
        SunSiteConstant.picturePath = picturePath;
    }

    public static int getThumbnailSize() {
        return thumbnailSize;
    }

    public static void setThumbnailSize(int thumbnailSize) {
        SunSiteConstant.thumbnailSize = thumbnailSize;
    }

    public static String getPathSeparator() {
        return pathSeparator;
    }

    public static void setPathSeparator(String pathSeparator) {
        SunSiteConstant.pathSeparator = pathSeparator;
    }

    public static long getSessionTimeout() {
        return sessionTimeout;
    }

    public static void setSessionTimeout(long sessionTimeout) {
        SunSiteConstant.sessionTimeout = sessionTimeout;
    }

    public static long getCacheTimeout() {
        return cacheTimeout;
    }

    public static void setCacheTimeout(long cacheTimeout) {
        SunSiteConstant.cacheTimeout = cacheTimeout;
    }

    public static int getCacheInitCapacity() {
        return cacheInitCapacity;
    }

    public static void setCacheInitCapacity(int cacheInitCapacity) {
        SunSiteConstant.cacheInitCapacity = cacheInitCapacity;
    }
}
