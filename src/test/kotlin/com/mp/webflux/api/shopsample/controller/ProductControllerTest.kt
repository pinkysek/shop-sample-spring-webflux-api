package com.mp.webflux.api.shopsample.controller

import com.mp.webflux.api.shopsample.dto.PagingDto
import com.mp.webflux.api.shopsample.dto.ProductResponseDto
import com.mp.webflux.api.shopsample.exception.ResourceNotFoundException
import com.mp.webflux.api.shopsample.service.ProductService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageRequest
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ProductControllerTest {

    private val productService = mockk<ProductService>()
    private lateinit var controller: ProductController

    @BeforeEach
    fun setUp() {
        controller = ProductController(productService)
    }

    @Test
    fun `Given valid id When getById Then returns product`() = runTest {
        val expected = aProduct()
        coEvery { productService.getById("1") } returns expected

        val result = controller.getById("1")

        assertEquals(expected, result)
        coVerify(exactly = 1) { productService.getById("1") }
    }

    @Test
    fun `Given nonexistent id When getById Then throws ResourceNotFoundException`() = runTest {
        coEvery { productService.getById("999") } throws ResourceNotFoundException("Product with ID: 999 was not found")

        assertFailsWith<ResourceNotFoundException> {
            controller.getById("999")
        }
    }

    @Test
    fun `Given valid pageable When getAllWithPaging Then returns paged result`() = runTest {
        val pageable = PageRequest.of(0, 10)
        val expected = PagingDto(
            items = listOf(aProduct()),
            pageNumber = 0,
            pageSize = 10,
            totalCount = 1,
            totalPages = 1,
            hasPrevious = false,
            hasNext = false
        )
        coEvery { productService.getAllWithPaging(pageable) } returns expected

        val result = controller.getAllWithPaging(pageable)

        assertEquals(expected, result)
    }

    private fun aProduct() = ProductResponseDto(
        id = "1",
        name = "Laptop",
        description = "High-performance laptop",
        sku = "SKU-001",
        price = BigDecimal("999.99"),
        imageUrl = null
    )
}
