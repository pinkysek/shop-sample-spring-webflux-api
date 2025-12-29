package com.mp.webflux.api.shopsample.repository

import com.mp.webflux.api.shopsample.document.Product
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : ReactiveMongoRepository<Product, String>