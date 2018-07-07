package sunnn.sunsite.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoClientOptionsFactoryBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.host}")
    private String host;

    @Value("${spring.data.mongodb.port}")
    private int port;

    @Value("${spring.data.mongodb.database}")
    private String database;

    @Bean
    @Primary
    public MongoClient createMongoClient() throws Exception {
        ServerAddress serverAddress = new ServerAddress(host, port);

//        MongoClientOptionsFactoryBean clientOptionsFactory = new MongoClientOptionsFactoryBean();
//        clientOptionsFactory.setConnectTimeout(2);
//        clientOptionsFactory.setThreadsAllowedToBlockForConnectionMultiplier(4);
//        clientOptionsFactory.setConnectTimeout(20000);
//        clientOptionsFactory.setMaxWaitTime(10000);
//
//        MongoClientOptions clientOptions = clientOptionsFactory.getObject();

        return new MongoClient(serverAddress);
    }

    @Bean
    @Primary
    public MongoDbFactory createMongoDbFactory(MongoClient mongoClient) {
        return new SimpleMongoDbFactory(mongoClient, database);
    }

    @Bean
    public MongoTemplate createMongoDbTemplate(MongoDbFactory factory) {
        return new MongoTemplate(factory);
    }


}
