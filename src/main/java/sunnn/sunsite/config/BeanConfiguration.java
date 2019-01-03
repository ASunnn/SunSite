package sunnn.sunsite.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import sunnn.sunsite.util.FileCache;
import sunnn.sunsite.util.SunSiteProperties;

import javax.sql.DataSource;

@Component
@DependsOn("sunSiteProperties")
public class BeanConfiguration {

    @Bean
    @Scope("singleton")
    public FileCache fileCache() {
        return new FileCache(SunSiteProperties.cacheTimeout);
    }

    @Bean
    public DataSource dataSource() {
//        HikariDataSource dataSource = DataSourceBuilder.create().type(HikariDataSource.class).build();
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setJdbcUrl(parseJdbcUrl());
        dataSource.setUsername(SunSiteProperties.username);
        dataSource.setPassword(SunSiteProperties.password);

        return dataSource;
    }

    private String parseJdbcUrl() {
        return "jdbc:mysql://"
                + SunSiteProperties.host
                + "/" + SunSiteProperties.database
                + "?characterEncoding=utf8&useSSL=false";
    }

//    @Bean
//    public Logger logger() {
//        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
//
//        RollingFileAppender fileAppender = new RollingFileAppender();
//
//        fileAppender.setFile(parseLogFile());
//
//        TimeBasedRollingPolicy rollingPolicy = new TimeBasedRollingPolicy<>();
//        rollingPolicy.setMaxHistory(30);
//        rollingPolicy.setFileNamePattern(parseRollingLogFile());
//        fileAppender.setRollingPolicy(rollingPolicy);
//
//        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
//        encoder.setCharset(Charset.forName("utf-8"));
//        encoder.setPattern("%d{HH:mm:ss.SSS} %-5level --- [%thread] %logger : %msg%n");
//        encoder.setContext(context);
////        encoder.start();
//        fileAppender.setEncoder(encoder);
//
//        fileAppender.setContext(context);
//
////        fileAppender.start();
//
//        ch.qos.logback.classic.Logger logger = context.getLogger("Main");
//        logger.addAppender(fileAppender);
//        logger.setLevel(Level.WARN);
//        return logge
//    }

//    private String parseLogFile() {
//        int length = SunSiteProperties.logPath.length();
//        char lastChar = SunSiteProperties.logPath.charAt(length - 1);
//        if (lastChar == '\\' || lastChar == '/')
//            return SunSiteProperties.logPath + Constants.logFile;
//        else
//            return SunSiteProperties.logPath + File.separator + Constants.logFile;
//    }
//
//    // /home/sun/log/SunSite-%d{yyyy-MM-dd}.log
//
//    private String parseRollingLogFile() {
//        String logFile = parseLogFile();
//        int index = logFile.lastIndexOf('.');
//
//        return logFile.substring(0, index) + "-%d{yyyy-MM-dd}" + logFile.substring(index);
//    }
}
