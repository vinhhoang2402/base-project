package com.demo.projectbase.feature.home.di

import androidx.room.Room
import com.demo.projectbase.feature.home.data.repository.MovieRepositoryImpl
import com.demo.projectbase.feature.home.data.source.local.HomeDatabase
import com.demo.projectbase.feature.home.data.source.local.MIGRATION_1_2
import com.demo.projectbase.feature.home.data.source.remote.MovieApiService
import com.demo.projectbase.feature.home.data.source.remote.MovieRemoteDataSource
import com.demo.projectbase.feature.home.domain.repository.MovieRepository
import com.demo.projectbase.feature.home.domain.usecase.GetPopularMoviesUseCase
import com.demo.projectbase.feature.home.domain.usecase.LogoutUseCase
import com.demo.projectbase.feature.home.presentation.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val homeModule =
    module {
        single {
            Room.databaseBuilder(
                get(),
                HomeDatabase::class.java,
                "home_database",
            ).addMigrations(MIGRATION_1_2).build()
        }

        single { get<Retrofit>().create(MovieApiService::class.java) }
        single { MovieRemoteDataSource(get()) }
        single<MovieRepository> { MovieRepositoryImpl(get(), get()) }

        factory { LogoutUseCase(get()) }
        factory { GetPopularMoviesUseCase(get()) }

        viewModel<HomeViewModel> { HomeViewModel(get(), get(), get()) }
    }
