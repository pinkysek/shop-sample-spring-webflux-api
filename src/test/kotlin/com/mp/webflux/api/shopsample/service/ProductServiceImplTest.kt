package com.mp.webflux.api.shopsample.service

import com.mp.webflux.api.shopsample.document.Product
import com.mp.webflux.api.shopsample.dto.CreateProductRequestDto
import com.mp.webflux.api.shopsample.exception.ResourceConflictException
import com.mp.webflux.api.shopsample.exception.ResourceNotFoundException
import com.mp.webflux.api.shopsample.repository.ProductRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ProductServiceImplTest {

    private val productRepository = mockk<ProductRepository>()
    private val mongoTemplate = mockk<ReactiveMongoTemplate>()
    private val imageService = mockk<ImageService>()
    private lateinit var service: ProductServiceImpl

    @BeforeEach
    fun setUp() {
        service = ProductServiceImpl(productRepository, mongoTemplate, imageService)
    }

    @Test
    fun `Given valid request When create Then returns created product`() = runTest {
        val request = aCreateRequest()
        val savedProduct = aProduct()
        coEvery { productRepository.save(any()) } returns savedProduct
        every { imageService.fetchAndSave(any<UUID>()) } just runs

        val result = service.create(request)

        assertEquals(savedProduct.name, result.name)
        assertEquals(savedProduct.sku, result.sku)
        assertEquals(savedProduct.price, result.price)
        assertNotNull(result.imageUrl)
    }

    @Test
    fun `Given duplicate SKU When create Then throws ResourceConflictException`() = runTest {
        coEvery { productRepository.save(any()) } throws DuplicateKeyException("duplicate key")

        assertFailsWith<ResourceConflictException> {
            service.create(aCreateRequest())
        }
    }

    @Test
    fun `Given existing id When getById Then returns product`() = runTest {
        val product = aProduct()
        coEvery { productRepository.findById("1") } returns product

        val result = service.getById("1")

        assertEquals(product.id, result.id)
        assertEquals(product.name, result.name)
        assertEquals(product.sku, result.sku)
    }

    @Test
    fun `Given nonexistent id When getById Then throws ResourceNotFoundException`() = runTest {
        coEvery { productRepository.findById("999") } returns null

        assertFailsWith<ResourceNotFoundException> {
            service.getById("999")
        }
    }

    @Test
    fun `Given existing id When deleteById Then completes successfully`() = runTest {
        val product = aProduct()
        every { mongoTemplate.findAndRemove(any(), Product::class.java) } returns Mono.just(product)

        service.deleteById("1")

        verify(exactly = 1) { mongoTemplate.findAndRemove(any(), Product::class.java) }
    }

    @Test
    fun `Given nonexistent id When deleteById Then throws ResourceNotFoundException`() = runTest {
        every { mongoTemplate.findAndRemove(any(), Product::class.java) } returns Mono.empty()

        assertFailsWith<ResourceNotFoundException> {
            service.deleteById("999")
        }
    }

    @Test
    fun `Given products exist When getAllWithPaging Then returns correct paging metadata`() = runTest {
        val pageable = PageRequest.of(0, 10)
        val product = aProduct()
        every { mongoTemplate.find(any(), Product::class.java) } returns Flux.just(product)
        every { mongoTemplate.estimatedCount(Product::class.java) } returns Mono.just(1L)

        val result = service.getAllWithPaging(pageable)

        assertEquals(1, result.items.size)
        assertEquals(1, result.totalCount)
        assertEquals(1, result.totalPages)
        assertTrue(!result.hasPrevious)
        assertTrue(!result.hasNext)
    }

    @Test
    fun `Given empty collection When getAllWithPaging Then returns empty result`() = runTest {
        val pageable = PageRequest.of(0, 10)
        every { mongoTemplate.find(any(), Product::class.java) } returns Flux.empty()
        every { mongoTemplate.estimatedCount(Product::class.java) } returns Mono.just(0L)

        val result = service.getAllWithPaging(pageable)

        assertEquals(0, result.items.size)
        assertEquals(0, result.totalCount)
        assertEquals(0, result.totalPages)
    }

    private fun aCreateRequest() = CreateProductRequestDto(
        name = "Laptop",
        description = "High-performance laptop",
        sku = "SKU-001",
        price = BigDecimal("999.99")
    )

    private fun aProduct() = Product(
        id = "1",
        name = "Laptop",
        description = "High-performance laptop",
        sku = "SKU-001",
        price = BigDecimal("999.99"),
        imageUuid = UUID.randomUUID().toString()
    )
}
