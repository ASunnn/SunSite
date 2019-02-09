package sunnn.sunsite.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import sunnn.sunsite.exception.IllegalPropertiesException;
import sunnn.sunsite.exception.UnSupportSystemException;
import sunnn.sunsite.filter.RequestFilter;
import sunnn.sunsite.util.FileCache;
import sunnn.sunsite.util.InputDataScanner;
import sunnn.sunsite.util.SunSiteProperties;

import javax.servlet.MultipartConfigElement;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Component
public class BeanConfiguration {

    @Bean
    public SunSiteProperties sunSiteProperties() throws UnSupportSystemException, IOException, IllegalPropertiesException {
        try {
            loadSystemConfig();
        } catch (UnSupportSystemException | IOException | IllegalPropertiesException e) {
            Logger log = LoggerFactory.getLogger(SunSiteProperties.class);
            log.error("Start SunSite Failed : ", e);
            throw e;
        }
        return new SunSiteProperties();
    }

    private void loadSystemConfig() throws UnSupportSystemException, IOException, IllegalPropertiesException {
        File propertiesFile =
                new File(getPropertiesFilePath());
        InputStream is = new FileInputStream(propertiesFile);

        Properties properties = new Properties();
        properties.load(is);
        parseSunsiteProperties(properties);
    }

    private String getPropertiesFilePath() throws UnSupportSystemException {
        String sys = System.getProperty("os.name");

        if (sys.contains("Windows")) {
            return "C:\\ProgramData\\sunsite\\sunsite-dev.properties";
        } else if (sys.contains("Linux"))
            return "";

        throw new UnSupportSystemException(sys);
    }

    private void parseSunsiteProperties(Properties properties) throws IllegalPropertiesException {
        String port = properties.getProperty("port");
        if (port != null)
            SunSiteProperties.setPort(Integer.valueOf(port));


        String verifyCode = properties.getProperty("verifyCode");
        if (verifyCode == null)
            throw new IllegalPropertiesException("Cannot Find Properties 'verifyCode'");
        SunSiteProperties.setVerifyCode(verifyCode);


        String savePath = properties.getProperty("savePath");
        if (savePath == null)
            throw new IllegalPropertiesException("Cannot Find Properties 'savePath'");
        SunSiteProperties.setSavePath(savePath);

        String tempPath = properties.getProperty("tempPath");
        if (tempPath == null)
            throw new IllegalPropertiesException("Cannot Find Properties 'tempPath'");
        SunSiteProperties.setTempPath(tempPath);

        String cacheTimeout = properties.getProperty("cacheTimeout");
        if (cacheTimeout != null)
            SunSiteProperties.setCacheTimeout(Integer.valueOf(cacheTimeout));

        String sessionTimeout = properties.getProperty("sessionTimeout");
        if (sessionTimeout != null)
            SunSiteProperties.setSessionTimeout(Integer.valueOf(sessionTimeout));

        String missPicture = properties.getProperty("missPicture");
        if (missPicture != null)
            SunSiteProperties.setMissPicture(missPicture);

        String scanAutoFill = properties.getProperty("scanAutoFill");
        if (scanAutoFill != null)
            SunSiteProperties.setScanAutoFill(Boolean.valueOf(scanAutoFill));


        String host = properties.getProperty("host");
        if (host == null)
            throw new IllegalPropertiesException("Cannot Find Properties 'host'");
        SunSiteProperties.setHost(host);

        String database = properties.getProperty("database");
        if (database == null)
            throw new IllegalPropertiesException("Cannot Find Properties 'database'");
        SunSiteProperties.setDatabase(database);

        String username = properties.getProperty("username");
        if (username == null)
            throw new IllegalPropertiesException("Cannot Find Properties 'username'");
        SunSiteProperties.setUsername(username);

        String password = properties.getProperty("password");
        if (password == null)
            throw new IllegalPropertiesException("Cannot Find Properties 'password'");
        SunSiteProperties.setPassword(password);
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("320MB");
        factory.setMaxRequestSize("384MB");
        return factory.createMultipartConfig();
    }

    @Bean
    @DependsOn("sunSiteProperties")
    public FileCache fileCache() {
        return new FileCache(SunSiteProperties.cacheTimeout);
    }

    @Bean
    @Scope(value = "prototype")
    public InputDataScanner dataScanner() {
        return new InputDataScanner();
    }

    @Bean
    public RequestFilter requestFilter() {
        return new RequestFilter();
    }
}
