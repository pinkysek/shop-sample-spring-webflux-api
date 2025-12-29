package com.mp.webflux.api.shopsample.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

@Schema(description = "Product response data transfer object")
data class ProductResponseDto(

    @Schema(description = "Unique identifier of the product", example = "507f1f77bcf86cd799439011")
    val id: String?,

    @Schema(description = "Name of the product", example = "Laptop", required = true)
    val name: String,

    @Schema(
        description = "Detailed description of the product",
        example = "High-performance laptop with 16GB RAM",
        required = true
    )
    val description: String,

    @Schema(description = "Stock Keeping Unit - unique product code", example = "SKU-12345", required = true)
    val sku: String,

    @Schema(description = "Price of the product", example = "999.99", required = true)
    val price: BigDecimal,

    @Schema(description = "URL to the product image", example = "https://www.example.com/api/v1/images/uuid-here")
    val imageUrl: String?
)