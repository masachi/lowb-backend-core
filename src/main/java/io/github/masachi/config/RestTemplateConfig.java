package io.github.masachi.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import io.github.masachi.condition.RestTemplateCondition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Configuration
@Conditional(RestTemplateCondition.class)
public class RestTemplateConfig {

    @Value("${resttemplate.connectTimeOut:20000}")
    private int connectTimeOut;

    @Value("${resttemplate.readTimeOut:20000}")
    private int readTimeOut;

    @Bean
    @LoadBalanced
    @ConditionalOnMissingBean({RestTemplate.class})
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // Do any additional configuration here
        RestTemplate restTemplate = builder.build();

        final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeOut);
        factory.setReadTimeout(readTimeOut);

        restTemplate.setRequestFactory(factory);
        setFastJson(restTemplate);

        // TODO 这里errorHandler 可以不做


        return restTemplate;
    }

    private void setFastJson(RestTemplate restTemplate) {
        List<HttpMessageConverter<?>> httpMessageConverterList = restTemplate.getMessageConverters();
        Iterator<HttpMessageConverter<?>> iterator = httpMessageConverterList.iterator();
        while (iterator.hasNext()) {
            HttpMessageConverter<?> converter = iterator.next();
            if (converter instanceof StringHttpMessageConverter) {
                iterator.remove();
            }

            if (converter instanceof GsonHttpMessageConverter || converter instanceof MappingJackson2HttpMessageConverter) {
                iterator.remove();
            }

        }
        httpMessageConverterList.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        List<MediaType> supportedMediaTypes = new ArrayList<>(20);
        supportedMediaTypes.add(MediaType.APPLICATION_JSON);
        supportedMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        supportedMediaTypes.add(MediaType.APPLICATION_ATOM_XML);
        supportedMediaTypes.add(MediaType.MULTIPART_FORM_DATA);
        supportedMediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
        supportedMediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
        supportedMediaTypes.add(MediaType.APPLICATION_PDF);
        supportedMediaTypes.add(MediaType.APPLICATION_RSS_XML);
        supportedMediaTypes.add(MediaType.APPLICATION_XHTML_XML);
        supportedMediaTypes.add(MediaType.APPLICATION_XML);
        supportedMediaTypes.add(MediaType.IMAGE_GIF);
        supportedMediaTypes.add(MediaType.IMAGE_JPEG);
        supportedMediaTypes.add(MediaType.IMAGE_PNG);
        supportedMediaTypes.add(MediaType.TEXT_EVENT_STREAM);
        supportedMediaTypes.add(MediaType.TEXT_HTML);
        supportedMediaTypes.add(MediaType.TEXT_MARKDOWN);
        supportedMediaTypes.add(MediaType.TEXT_PLAIN);
        supportedMediaTypes.add(MediaType.TEXT_XML);
        fastJsonHttpMessageConverter.setSupportedMediaTypes(supportedMediaTypes);

        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.DisableCircularReferenceDetect);

        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
        httpMessageConverterList.add(fastJsonHttpMessageConverter);
    }
}
