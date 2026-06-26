package com.demo.projectbase.feature.home.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String?,
    val voteAverage: Double,
    val releaseDate: String,
)
