package com.demo.projectbase.feature.home.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.demo.projectbase.feature.home.domain.model.Movie

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String?,
    val voteAverage: Double,
    val releaseDate: String,
)

fun MovieEntity.toDomain() = Movie(id, title, overview, posterUrl, voteAverage, releaseDate)
fun Movie.toEntity() = MovieEntity(id, title, overview, posterUrl, voteAverage, releaseDate)
