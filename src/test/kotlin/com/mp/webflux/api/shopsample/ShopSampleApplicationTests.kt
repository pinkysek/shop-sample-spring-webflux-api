package com.mp.webflux.api.shopsample

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.mongodb.MongoDBContainer

@SpringBootTest
@Testcontainers
class ShopSampleApplicationTests {

    companion object {
        @Container
        @ServiceConnection
        val mongo = MongoDBContainer("mongo:7.0")
    }

    @Test
    fun contextLoads() {
    }
}
