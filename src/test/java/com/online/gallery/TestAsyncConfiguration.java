package com.online.gallery;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SyncTaskExecutor;

import java.util.concurrent.Executor;

@TestConfiguration
public class TestAsyncConfiguration {
    @Bean(name = "syncTaskExecutor")
    public Executor taskExecutor() {
        return new SyncTaskExecutor();
    }

}
