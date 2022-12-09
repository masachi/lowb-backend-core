package io.github.masachi.config;

import io.github.masachi.condition.ElasticsearchCondition;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(
        basePackages = "io.github.masachi.es.repository"
)
@Conditional(ElasticsearchCondition.class)
public class ElasticSearchConfig {

    private static final Logger log = LoggerFactory.getLogger(ElasticSearchConfig.class);
    @Value("${elasticsearch.host}")
    private String host;
    @Value("${elasticsearch.username}")
    private String username;
    @Value("${elasticsearch.password}")
    private String password;

    @Bean
    public RestHighLevelClient client() {
        log.info("初始化 -> [{}]", "Elastic Search Initialize Start");
        ClientConfiguration clientConfiguration
                = ClientConfiguration.builder()
                .connectedTo(host)
                .usingSsl()
                .withBasicAuth(username, password)
                .build();

        return RestClients.create(clientConfiguration).rest();
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        return new ElasticsearchRestTemplate(client());
    }
}
