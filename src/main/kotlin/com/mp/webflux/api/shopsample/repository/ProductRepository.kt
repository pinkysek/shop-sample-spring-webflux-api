package com.mp.webflux.api.shopsample.repository

import com.mp.webflux.api.shopsample.document.Product
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : CoroutineCrudRepository<Product, String> {

    suspend fun findAllBy(pageable: Pageable): Flow<Product>
}
