package com.mp.webflux.api.shopsample.util

import com.mp.webflux.api.shopsample.dto.ApiErrorDto
import com.mp.webflux.api.shopsample.exception.ResourceConflictException
import com.mp.webflux.api.shopsample.exception.ResourceNotFoundException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import java.time.ZonedDateTime

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = KotlinLogging.logger {}

    @ExceptionHandler(ResourceNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleResourceNotFoundException(ex: ResourceNotFoundException): ApiErrorDto {
        return ApiErrorDto(
            status = HttpStatus.NOT_FOUND.name,
            timestamp = ZonedDateTime.now(),
            message = ex.message
        )
    }

    @ExceptionHandler(ResourceConflictException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleResourceConflictException(ex: ResourceConflictException): ApiErrorDto {
        return ApiErrorDto(
            status = HttpStatus.CONFLICT.name,
            timestamp = ZonedDateTime.now(),
            message = ex.message
        )
    }

    @ExceptionHandler(WebExchangeBindException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleWebExchangeBindException(ex: WebExchangeBindException): ApiErrorDto {
        val errors = ex.bindingResult.fieldErrors.joinToString(", ") {
            "${it.field}: ${it.defaultMessage ?: "Invalid value"}"
        }

        return ApiErrorDto(
            status = HttpStatus.BAD_REQUEST.name,
            timestamp = ZonedDateTime.now(),
            message = "Validation failed",
            detailedMessage = errors
        )
    }

    @ExceptionHandler(AuthorizationDeniedException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleAuthorizationDeniedException(ex : AuthorizationDeniedException) : ApiErrorDto {
        return ApiErrorDto(
            status = HttpStatus.FORBIDDEN.name,
            timestamp = ZonedDateTime.now(),
            message = ex.message
        )
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleUnexpectedException(ex: Exception): ApiErrorDto {
        log.error(ex) { "Unexpected error occurred" }

        return ApiErrorDto(
            status = HttpStatus.INTERNAL_SERVER_ERROR.name,
            timestamp = ZonedDateTime.now(),
            message = "An unexpected error occurred"
        )
    }
}