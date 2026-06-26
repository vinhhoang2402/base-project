package com.demo.projectbase.feature.home.data.source.remote

import com.demo.projectbase.core.network.safeSuspend
import com.demo.projectbase.feature.home.data.source.remote.dto.MovieDto
import com.demo.projectbase.feature.home.domain.model.Movie

private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"

data class PopularMoviesPage(val movies: List<Movie>, val totalPages: Int)

class MovieRemoteDataSource(private val apiService: MovieApiService) {
    suspend fun getPopularMovies(page: Int = 1): Result<PopularMoviesPage> = safeSuspend {
        val response = apiService.getPopularMovies(page)
        PopularMoviesPage(
            movies = response.results.map { it.toDomain() },
            totalPages = response.totalPages,
        )
    }
}

private fun MovieDto.toDomain() = Movie(
    id = id,
    title = title,
    overview = overview,
    posterUrl = posterPath?.let { "$IMAGE_BASE_URL$it" },
    voteAverage = voteAverage.toDoubleOrNull() ?: 0.0,
    releaseDate = releaseDate,
)
