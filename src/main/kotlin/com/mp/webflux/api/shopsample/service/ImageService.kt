package com.mp.webflux.api.shopsample.service

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodyOrNull
import java.util.*
import kotlin.random.Random

@Service
class ImageService(
    private val imageWebClient: WebClient
) {
        private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

        companion object {
            private val log = KotlinLogging.logger {}
            private val random = Random
        }

    fun fetchAndSave(imageUuid: UUID) {
        scope.launch {
            val bytes = runCatching {
                imageWebClient.get()
                    .uri("/id/${random.nextInt(200)}/200/300")
                    .retrieve()
                    .awaitBodyOrNull<ByteArray>()
            }.getOrNull()

            if (bytes != null) saveImageToS3(imageUuid, bytes)
        }
    }

    suspend fun saveImageToS3(uuid: UUID, data: ByteArray) {
        runCatching {
            log.info { "Send image to S3 with UUID: $uuid and hash: ${data.hashCode()}" }
            delay(500)
        }.onSuccess { log.info { "Successfully saved image to S3 with UUID: $uuid" } }
            .onFailure { log.error(it) { "Error saving image to S3 with UUID: $uuid" } }
    }
}