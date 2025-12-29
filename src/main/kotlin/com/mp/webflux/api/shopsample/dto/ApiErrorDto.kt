package com.mp.webflux.api.shopsample.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import java.time.ZonedDateTime

@Schema(description = "Api error data transfer object")
data class ApiErrorDto(

    @Schema(description = "The HTTP status of the error.", example = "BAD_REQUEST")
    val status: String,

    @Schema(
        description = "The timestamp when the error occurred [date-time-format](https://tools.ietf.org/html/rfc3339#section-5.6).",
        example = "2024-12-01T12:00:00+01:00"
    )
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    val timestamp: ZonedDateTime,

    @Schema(description = "The error message.", example = "An error occurred.")
    val message: String?,

    @Schema(
        description = "TThe detailed error message, if available (This field is included only if it is not null).",
        example = "The value of the field 'name' must be a string."
    )
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val detailedMessage : String? = null
)