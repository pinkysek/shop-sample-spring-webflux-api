package com.mp.webflux.api.shopsample.repository

import com.mp.webflux.api.shopsample.document.Product
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.mongodb.MongoDBContainer
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@DataMongoTest
@Testcontainers
class ProductRepositoryTest {

    companion object {
        @Container
        @ServiceConnection
        val mongo = MongoDBContainer("mongo:7.0")
    }

    @Autowired
    lateinit var repository: ProductRepository

    @BeforeEach
    fun setUp() = runTest {
        repository.deleteAll()
    }

    @Test
    fun `Given valid product When save Then persists and returns with generated id`() = runTest {
        val saved = repository.save(aProduct(sku = "SKU-001"))

        assertNotNull(saved.id)
        assertEquals("SKU-001", saved.sku)
    }

    @Test
    fun `Given saved product When findById Then returns product`() = runTest {
        val saved = repository.save(aProduct(sku = "SKU-001"))

        val found = repository.findById(saved.id!!)

        assertNotNull(found)
        assertEquals(saved.id, found.id)
        assertEquals(saved.name, found.name)
    }

    @Test
    fun `Given nonexistent id When findById Then returns null`() = runTest {
        val found = repository.findById("nonexistent-id")

        assertNull(found)
    }

    @Test
    fun `Given duplicate SKU When save Then throws DuplicateKeyException`() = runTest {
        repository.save(aProduct(sku = "SKU-DUPE"))

        assertFailsWith<DuplicateKeyException> {
            repository.save(aProduct(sku = "SKU-DUPE"))
        }
    }

    @Test
    fun `Given multiple products When findAllBy with pageable Then returns correct page`() = runTest {
        repository.save(aProduct(sku = "SKU-001", name = "Aardvark"))
        repository.save(aProduct(sku = "SKU-002", name = "Banana"))
        repository.save(aProduct(sku = "SKU-003", name = "Cherry"))

        val pageable = PageRequest.of(0, 2, Sort.by("name").ascending())
        val page = repository.findAllBy(pageable).toList()

        assertEquals(2, page.size)
        assertEquals("Aardvark", page[0].name)
        assertEquals("Banana", page[1].name)
    }

    @Test
    fun `Given saved product When deleteById Then product is removed`() = runTest {
        val saved = repository.save(aProduct(sku = "SKU-001"))

        repository.deleteById(saved.id!!)

        assertNull(repository.findById(saved.id!!))
    }

    private fun aProduct(sku: String, name: String = "Test Product") = Product(
        name = name,
        description = "Test description",
        sku = sku,
        price = BigDecimal("99.99")
    )
}
