package com.demo.projectbase.feature.home.data.source.remote.dto

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

private object DoubleAsStringSerializer : KSerializer<String> {
    override val descriptor = PrimitiveSerialDescriptor("DoubleAsString", PrimitiveKind.DOUBLE)
    override fun deserialize(decoder: Decoder): String = decoder.decodeDouble().toString()
    override fun serialize(encoder: Encoder, value: String) = encoder.encodeDouble(value.toDoubleOrNull() ?: 0.0)
}

@Serializable
data class MovieDto(
    @SerialName("id") val id: Int,
    @SerialName("title") val title: String,
    @SerialName("overview") val overview: String,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("vote_average") @Serializable(with = DoubleAsStringSerializer::class) val voteAverage: String,
    @SerialName("release_date") val releaseDate: String = "",
)

@Serializable
data class PopularMoviesResponse(
    @SerialName("page") val page: Int,
    @SerialName("results") val results: List<MovieDto>,
    @SerialName("total_pages") val totalPages: Int = Int.MAX_VALUE,
)
