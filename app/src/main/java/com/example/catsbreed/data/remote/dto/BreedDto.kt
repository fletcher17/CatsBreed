package com.example.catsbreed.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BreedDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String = "",
    @SerialName("origin") val origin: String = "",
    @SerialName("temperament") val temperament: String = "",
    @SerialName("description") val description: String = "",
    @SerialName("life_span") val lifeSpan: String = "",
    @SerialName("reference_image_id") val referenceImageId: String? = null,
    @SerialName("image") val image: ImageDto? = null
)

@Serializable
data class ImageDto(
    @SerialName("id") val id: String? = null,
    @SerialName("url") val url: String? = null,
    @SerialName("width") val width: Int? = null,
    @SerialName("height") val height: Int? = null
)
