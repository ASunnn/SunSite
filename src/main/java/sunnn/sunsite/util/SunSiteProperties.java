package sunnn.sunsite.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sunsite")
@PropertySource(value = "file:F:\\sunsite.properties")
//@PropertySource(value = "file:${properties-file}")
public class SunSiteProperties {

    public static int port;

    public static String verifyCode;

//    public static String logPath;

    public static String savePath;

    public static String tempPath;

    public static long cacheTimeout;

    public static long sessionTimeout;

    public static String missPicture;

    public static String host;

    public static String database;

    public static String username;

    public static String password;

    public static void setPort(int port) {
        SunSiteProperties.port = port;
    }

    public static void setVerifyCode(String verifyCode) {
        SunSiteProperties.verifyCode = verifyCode;
    }

//    public static void setLogPath(String logPath) {
//        SunSiteProperties.logPath = logPath;
//    }

    public static void setSavePath(String savePath) {
        SunSiteProperties.savePath = savePath;
    }

    public static void setTempPath(String tempPath) {
        SunSiteProperties.tempPath = tempPath;
    }

    public static void setCacheTimeout(long cacheTimeout) {
        SunSiteProperties.cacheTimeout = cacheTimeout;
    }

    public static void setSessionTimeout(long sessionTimeout) {
        SunSiteProperties.sessionTimeout = sessionTimeout;
    }

    public static void setMissPicture(String missPicture) {
        SunSiteProperties.missPicture = missPicture;
    }

    public static void setHost(String host) {
        SunSiteProperties.host = host;
    }

    public static void setDatabase(String database) {
        SunSiteProperties.database = database;
    }

    public static void setUsername(String username) {
        SunSiteProperties.username = username;
    }

    public static void setPassword(String password) {
        SunSiteProperties.password = password;
    }
}
