package io.github.masachi.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

@Configuration
public class SpringBootContextStartListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(@NotNull ContextRefreshedEvent event) {
        // TODO  注册与 反注册
        // TODO 更新数据库全量 controller 和 method
        System.out.println("Context Start Event received.");
    }
}
