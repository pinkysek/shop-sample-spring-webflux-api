package com.mp.webflux.api.shopsample.service

import com.mp.webflux.api.shopsample.dto.CreateProductRequestDto
import com.mp.webflux.api.shopsample.dto.PagingDto
import com.mp.webflux.api.shopsample.dto.ProductResponseDto
import org.springframework.data.domain.Pageable

interface ProductService {

    suspend fun create(dto: CreateProductRequestDto) : ProductResponseDto
    suspend fun getById(id: String) : ProductResponseDto
    suspend fun deleteById(id: String)
    suspend fun getAllWithPaging(pageable: Pageable) : PagingDto<ProductResponseDto>
}