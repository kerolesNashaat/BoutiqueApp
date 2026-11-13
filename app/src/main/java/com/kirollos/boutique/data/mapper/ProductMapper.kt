package com.kirollos.boutique.data.mapper

import com.kirollos.boutique.data.dto.DimensionsDto
import com.kirollos.boutique.data.dto.MetaDto
import com.kirollos.boutique.data.dto.ProductDto
import com.kirollos.boutique.data.dto.ProductListDto
import com.kirollos.boutique.data.dto.ReviewDto
import com.kirollos.boutique.domain.model.Dimensions
import com.kirollos.boutique.domain.model.Meta
import com.kirollos.boutique.domain.model.Product
import com.kirollos.boutique.domain.model.ProductList
import com.kirollos.boutique.domain.model.Review

fun ProductListDto.toDomain(): ProductList {
    return ProductList(
        limit = limit ?: 0,
        products = productsDto?.mapNotNull { it?.toDomain() }.orEmpty(),
        skip = skip ?: 0,
        total = total ?: 0
    )
}

fun ProductDto.toDomain(): Product {
    return Product(
        availabilityStatus = availabilityStatus.orEmpty(),
        brand = brand.orEmpty(),
        category = category.orEmpty(),
        description = description.orEmpty(),
        dimensions = dimensionsDto?.toDomain() ?: Dimensions(0.0, 0.0, 0.0),
        discountPercentage = discountPercentage ?: 0.0,
        id = id ?: 0,
        images = images?.filterNotNull().orEmpty(),
        meta = metaDto?.toDomain() ?: Meta("", "", "", ""),
        minimumOrderQuantity = minimumOrderQuantity ?: 0,
        price = price ?: 0.0,
        rating = rating ?: 0.0,
        returnPolicy = returnPolicy.orEmpty(),
        reviews = reviewDtos?.mapNotNull { it?.toDomain() }.orEmpty(),
        shippingInformation = shippingInformation.orEmpty(),
        sku = sku.orEmpty(),
        stock = stock ?: 0,
        tags = tags?.filterNotNull().orEmpty(),
        thumbnail = thumbnail.orEmpty(),
        title = title.orEmpty(),
        warrantyInformation = warrantyInformation.orEmpty(),
        weight = weight ?: 0
    )
}

fun DimensionsDto.toDomain(): Dimensions {
    return Dimensions(
        depth = depth ?: 0.0,
        height = height ?: 0.0,
        width = width ?: 0.0
    )
}

fun MetaDto.toDomain(): Meta {
    return Meta(
        barcode = barcode.orEmpty(),
        createdAt = createdAt.orEmpty(),
        qrCode = qrCode.orEmpty(),
        updatedAt = updatedAt.orEmpty()
    )
}

fun ReviewDto.toDomain(): Review {
    return Review(
        comment = comment.orEmpty(),
        date = date.orEmpty(),
        rating = rating ?: 0,
        reviewerEmail = reviewerEmail.orEmpty(),
        reviewerName = reviewerName.orEmpty()
    )
}
