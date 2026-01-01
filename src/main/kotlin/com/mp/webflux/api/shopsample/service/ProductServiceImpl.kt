package com.mp.webflux.api.shopsample.service

import com.mp.webflux.api.shopsample.document.Product
import com.mp.webflux.api.shopsample.dto.CreateProductRequestDto
import com.mp.webflux.api.shopsample.dto.PagingDto
import com.mp.webflux.api.shopsample.dto.ProductResponseDto
import com.mp.webflux.api.shopsample.exception.ResourceNotFoundException
import com.mp.webflux.api.shopsample.repository.ProductRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.findAndRemove
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import java.util.*
import kotlin.math.ceil

@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val mongoTemplate: ReactiveMongoTemplate,
    private val imageService: ImageService
) : ProductService {

    companion object {
        private val log = KotlinLogging.logger {}
        private const val IMAGE_BASE_URL = "https://www.example.com/api/v1/images"
    }

    override suspend fun create(dto: CreateProductRequestDto): ProductResponseDto {
        log.info { "Creating new product: $dto" }
        val imageUuid = UUID.randomUUID()
        val product = Product(
            name = dto.name,
            description = dto.description,
            sku = dto.sku,
            price = dto.price,
            imageUuid = imageUuid.toString()
        )
        val savedProduct = productRepository.save(product)

        imageService.fetchAndSave(imageUuid)
        return savedProduct.toResponseDto()
    }

    override suspend fun getById(id: String): ProductResponseDto {
        log.info { "Get product with ID: $id" }

        return productRepository.findById(id)?.toResponseDto()
            ?: throw ResourceNotFoundException("Product with ID: $id was not found")
    }

    override suspend fun deleteById(id: String) {
        log.info { "Deleting product with ID: $id" }
        mongoTemplate.findAndRemove<Product>(
            Query.query(Criteria.where("_id").`is`(id))
        ).awaitSingleOrNull()?.let { log.info { "Product: $it was successfully deleted" } } ?: run {
            throw ResourceNotFoundException("Product with ID: $id was not found")
        }
    }

    override suspend fun getAllWithPaging(pageable: Pageable): PagingDto<ProductResponseDto> {
        log.info { "Get all products with pageable: $pageable" }

        val (products, totalCount) = coroutineScope {
            val productsDeferred = async {
                val query = Query().with(pageable)
                mongoTemplate.find<Product>(query)
                    .asFlow()
                    .map { it.toResponseDto() }
                    .toList()
            }
            val countDeferred = async {
                productRepository.count()
            }
            productsDeferred.await() to countDeferred.await()
        }
        val totalPages = ceil(totalCount.toDouble() / pageable.pageSize.toDouble()).toInt()

        return PagingDto(
            items = products,
            pageNumber = pageable.pageNumber,
            pageSize = pageable.pageSize,
            totalCount = totalCount.toInt(),
            totalPages = totalPages,
            hasPrevious = pageable.pageNumber > 0,
            hasNext = pageable.pageNumber + 1 < totalPages
        )
    }

    private fun Product.toResponseDto() = ProductResponseDto(
        id = id,
        name = name,
        description = description,
        sku = sku,
        price = price,
        imageUrl = imageUuid?.let { "$IMAGE_BASE_URL/$it" }
    )
}