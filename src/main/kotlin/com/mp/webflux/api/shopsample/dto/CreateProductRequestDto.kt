package com.mp.webflux.api.shopsample.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.math.BigDecimal

@Schema(description = "Request data transfer object for creating a new product")
data class CreateProductRequestDto(

    @field:NotBlank(message = "Name is required")
    @field:Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    @Schema(description = "Name of the product (3-100 characters)", example = "Laptop", required = true)
    val name: String,

    @field:NotBlank(message = "Description is required")
    @Schema(description = "Detailed description of the product", example = "High-performance laptop with 16GB RAM", required = true)
    val description: String,

    @field:NotBlank(message = "SKU is required")
    @field:Pattern(
        regexp = "^[A-Z0-9-]+$",
        message = "SKU must contain only uppercase letters, numbers and hyphens"
    )
    @Schema(description = "Stock Keeping Unit - unique product code (uppercase letters, numbers, and hyphens only)", example = "SKU-12345", required = true)
    val sku: String,

    @field:Positive(message = "Price must be positive")
    @field:DecimalMin(value = "0.01", message = "Price must be at least 0.01")
    @Schema(description = "Price of the product (minimum 0.01)", example = "999.99", required = true)
    val price: BigDecimal
)