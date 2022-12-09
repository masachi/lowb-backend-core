package io.github.masachi.config;



import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.github.masachi.condition.MongoDBCondition;
import io.github.masachi.utils.BaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;


import java.util.Objects;

@Configuration
@DependsOn("springBeanUtils")
@Conditional(MongoDBCondition.class)
public class MongoDBConfig {

    /**
     * Logger
     */
    private static final Logger log = LoggerFactory.getLogger(MongoDBConfig.class);

    @Value("${spring.data.mongodb.uri}")
    private String primaryUri;

    @Value("${spring.data.mongodb.database}")
    private String database;

    @Primary
    @Bean(name = "mongoTemplate")
    public MongoTemplate getMongoTemplatePrimary() {
        log.info("初始化 -> [{}]", "MongoTemplate init Start");
        return new MongoTemplate(Objects.requireNonNull(getMongoDbFactory(primaryUri)));
    }


    private MongoDatabaseFactory getMongoDbFactory(String mongodbUrl) {
        if (BaseUtil.isEmpty(mongodbUrl)) {
            return null;
        }

        ConnectionString connectionString = new ConnectionString(mongodbUrl);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();
        MongoClient mongoClient = MongoClients.create(settings);

        return new SimpleMongoClientDatabaseFactory(mongoClient, database);
    }
}

