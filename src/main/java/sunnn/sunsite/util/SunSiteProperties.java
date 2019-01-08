package sunnn.sunsite.util;

import java.io.File;

public class SunSiteProperties {

    public static int port = 80;

    public static String verifyCode;

    public static String savePath;

    public static String tempPath;

    public static long cacheTimeout = 600000;

    public static long sessionTimeout = 1200000;

    public static String missPicture = "";

    public static boolean scanAutoFill = false;

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

    public static void setSavePath(String savePath) {
        if (!savePath.endsWith("/") && !savePath.endsWith("\\"))
            SunSiteProperties.savePath = savePath + File.separator;
        else
            SunSiteProperties.savePath = savePath;
    }

    public static void setTempPath(String tempPath) {
        if (!tempPath.endsWith("/") && !tempPath.endsWith("\\"))
            SunSiteProperties.tempPath = tempPath + File.separator;
        else
            SunSiteProperties.tempPath = tempPath;
    }

    public static void setCacheTimeout(long cacheTimeout) {
        if (cacheTimeout < 5)
            cacheTimeout = 5;
        SunSiteProperties.cacheTimeout = cacheTimeout * 60000;
    }

    public static void setSessionTimeout(long sessionTimeout) {
        SunSiteProperties.sessionTimeout = sessionTimeout * 60000;
    }

    public static void setMissPicture(String missPicture) {
        SunSiteProperties.missPicture = missPicture;
    }

    public static void setScanAutoFill(boolean scanAutoFill) {
        SunSiteProperties.scanAutoFill = scanAutoFill;
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
