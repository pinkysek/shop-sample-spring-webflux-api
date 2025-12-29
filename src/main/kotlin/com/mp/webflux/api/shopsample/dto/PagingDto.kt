package com.mp.webflux.api.shopsample.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Paging data transfer object")
data class PagingDto<T>(

    @Schema(description = "The items in the current page.", example = "[{...},{...}]")
    val items: List<T>,

    @Schema(description = "The current page number.", example = "1")
    val pageNumber: Int,

    @Schema(description = "The number of items per page.", example = "10")
    val pageSize: Int,

    @Schema(description = "The total number of items.", example = "100")
    val totalCount: Int,

    @Schema(description = "The total number of pages.", example = "10")
    val totalPages: Int,

    @Schema(description = "Indicates whether there is a previous page.", example = "true")
    val hasPrevious: Boolean,

    @Schema(description = "Indicates whether there is a next page.", example = "true")
    val hasNext : Boolean
)