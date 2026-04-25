package com.mp.webflux.api.shopsample.controller

import com.mp.webflux.api.shopsample.dto.CreateProductRequestDto
import com.mp.webflux.api.shopsample.dto.ProductResponseDto
import com.mp.webflux.api.shopsample.exception.ResourceConflictException
import com.mp.webflux.api.shopsample.exception.ResourceNotFoundException
import com.mp.webflux.api.shopsample.service.ProductService
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AdminProductControllerTest {

    private val productService = mockk<ProductService>()
    private lateinit var controller: AdminProductController

    @BeforeEach
    fun setUp() {
        controller = AdminProductController(productService)
    }

    @Test
    fun `Given valid request When create Then returns created product`() = runTest {
        val request = aCreateRequest()
        val expected = aProduct()
        coEvery { productService.create(request) } returns expected

        val result = controller.create(request)

        assertEquals(expected, result)
        coVerify(exactly = 1) { productService.create(request) }
    }

    @Test
    fun `Given duplicate SKU When create Then throws ResourceConflictException`() = runTest {
        val request = aCreateRequest()
        coEvery { productService.create(request) } throws ResourceConflictException("Product with SKU: SKU-001 already exists")

        assertFailsWith<ResourceConflictException> {
            controller.create(request)
        }
    }

    @Test
    fun `Given existing id When deleteById Then completes successfully`() = runTest {
        coJustRun { productService.deleteById("1") }

        controller.deleteById("1")

        coVerify(exactly = 1) { productService.deleteById("1") }
    }

    @Test
    fun `Given nonexistent id When deleteById Then throws ResourceNotFoundException`() = runTest {
        coEvery { productService.deleteById("999") } throws ResourceNotFoundException("Product with ID: 999 was not found")

        assertFailsWith<ResourceNotFoundException> {
            controller.deleteById("999")
        }
    }

    private fun aCreateRequest() = CreateProductRequestDto(
        name = "Laptop",
        description = "High-performance laptop",
        sku = "SKU-001",
        price = BigDecimal("999.99")
    )

    private fun aProduct() = ProductResponseDto(
        id = "1",
        name = "Laptop",
        description = "High-performance laptop",
        sku = "SKU-001",
        price = BigDecimal("999.99"),
        imageUrl = null
    )
}
