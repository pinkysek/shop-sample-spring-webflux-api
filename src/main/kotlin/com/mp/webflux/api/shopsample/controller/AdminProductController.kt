package com.mp.webflux.api.shopsample.controller

import com.mp.webflux.api.shopsample.dto.CreateProductRequestDto
import com.mp.webflux.api.shopsample.dto.ProductResponseDto
import com.mp.webflux.api.shopsample.service.ProductService
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/admin/products")
@Tag(name = "Admin Products", description = "API for managing products.")
@PreAuthorize("hasRole('ADMIN')")
class AdminProductController(private val productService: ProductService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun create(@Valid @RequestBody dto: CreateProductRequestDto): ProductResponseDto =
        productService.create(dto)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteById(@PathVariable id: String) =
        productService.deleteById(id)
}
