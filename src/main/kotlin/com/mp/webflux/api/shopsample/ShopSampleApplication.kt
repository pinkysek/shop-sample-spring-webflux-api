package com.mp.webflux.api.shopsample

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ShopSampleApplication

fun main(args: Array<String>) {
	runApplication<ShopSampleApplication>(*args)
}