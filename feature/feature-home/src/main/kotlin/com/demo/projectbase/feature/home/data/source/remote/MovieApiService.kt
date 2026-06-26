package com.demo.projectbase.feature.home.data.source.remote

import com.demo.projectbase.feature.home.data.source.remote.dto.PopularMoviesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApiService {
    @GET("movie/popular")
    suspend fun getPopularMovies(@Query("page") page: Int = 1): PopularMoviesResponse
}
