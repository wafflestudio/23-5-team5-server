package com.team5.studygroup.helper.mock

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

class MockRedis : ApplicationContextInitializer<ConfigurableApplicationContext> {
    companion object {
        private val redisContainer =
            GenericContainer(DockerImageName.parse("redis:7-alpine"))
                .withExposedPorts(6379)
                .apply { start() }
    }

    override fun initialize(context: ConfigurableApplicationContext) {
        System.setProperty("spring.data.redis.host", redisContainer.host)
        System.setProperty("spring.data.redis.port", redisContainer.getMappedPort(6379).toString())
    }
}
