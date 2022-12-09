package io.github.masachi.config;

import io.github.masachi.condition.MinIOCondition;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(MinIOCondition.class)
public class MinIOConfig {

    @Value("${mioio.endpoint}")
    String endpoint;

    @Value("${mioio.accessKey}")
    String accessKey;

    @Value("${mioio.secretKey}")
    String secretKey;

    @Bean
    public MinioClient minioClient() {
        // 创建 MinioClient 客户端
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
