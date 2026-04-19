package com.mp.webflux.api.shopsample.controller

import com.mp.webflux.api.shopsample.dto.PagingDto
import com.mp.webflux.api.shopsample.dto.ProductResponseDto
import com.mp.webflux.api.shopsample.service.ProductService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Products", description = "API for browsing products.")
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
class ProductController(private val productService: ProductService) {

    @GetMapping("/{id}")
    suspend fun getById(@PathVariable id: String): ProductResponseDto =
        productService.getById(id)

    @GetMapping("/paging")
    suspend fun getAllWithPaging(
        @PageableDefault(size = 10, page = 0, sort = ["name,asc"])
        pageable: Pageable
    ): PagingDto<ProductResponseDto> =
        productService.getAllWithPaging(pageable)
}
