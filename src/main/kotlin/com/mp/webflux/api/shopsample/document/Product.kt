package com.mp.webflux.api.shopsample.document

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.math.BigDecimal

@Document(collection = "product")
data class Product(

    @Id
    val id: String? = null,

    val name: String,

    val description: String,

    @Indexed(unique = true)
    val sku: String,

    val price: BigDecimal,

    @Field("image_uuid")
    val imageUuid: String? = null
)