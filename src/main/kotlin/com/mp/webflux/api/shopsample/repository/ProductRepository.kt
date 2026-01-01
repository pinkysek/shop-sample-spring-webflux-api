package com.mp.webflux.api.shopsample.repository

import com.mp.webflux.api.shopsample.document.Product
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : CoroutineCrudRepository<Product, String>