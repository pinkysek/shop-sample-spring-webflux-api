package com.mp.webflux.api.shopsample.controller

import com.mp.webflux.api.shopsample.dto.CreateProductRequestDto
import com.mp.webflux.api.shopsample.dto.PagingDto
import com.mp.webflux.api.shopsample.dto.ProductResponseDto
import com.mp.webflux.api.shopsample.service.ProductService
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Products", description = "API for managing products.")
class ProductController(val productService: ProductService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    suspend fun createProduct(@Valid @RequestBody dto: CreateProductRequestDto): ProductResponseDto =
        productService.create(dto)

    @GetMapping(value = ["/{id}"])
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    suspend fun getProductById(@PathVariable id: String): ProductResponseDto =
        productService.getById(id)

    @DeleteMapping(value = ["/{id}"])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    suspend fun deleteById(@PathVariable id: String) =
        productService.deleteById(id)

    @GetMapping(value = ["/paging"])
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    suspend fun getAllProductsWithPaging(
        @PageableDefault(size = 10, page = 0, sort = ["name,asc"])
        pageable: Pageable
    ): PagingDto<ProductResponseDto> =
        productService.getAllWithPaging(pageable)
}