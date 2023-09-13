package io.github.masachi.config;

import io.github.masachi.annotation.EnableJobRunner;
import org.jobrunr.configuration.JobRunr;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.server.JobActivator;
import org.jobrunr.storage.InMemoryStorageProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@ConditionalOnBean(annotation = EnableJobRunner.class)
@Deprecated
public class JobRunrConfig {

    @Bean
    public JobActivator jobActivator(ApplicationContext applicationContext) {
        return applicationContext::getBean;
    }

    @Bean
    public JobScheduler initJobRunr(DataSource dataSource, JobActivator jobActivator) {
        return JobRunr.configure()
                .useJobActivator(jobActivator)
                .useStorageProvider(new InMemoryStorageProvider())
                .useBackgroundJobServer()
                .useJmxExtensions()
                .useDashboard(23334)
                .initialize().getJobScheduler();
    }
}
