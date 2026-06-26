package com.demo.projectbase.feature.auth.di

import com.demo.projectbase.core.network.AuthInterceptor
import com.demo.projectbase.core.network.NativeLib
import com.demo.projectbase.core.network.SecurePreferencesManager
import com.demo.projectbase.feature.auth.data.repository.AuthRepositoryImpl
import com.demo.projectbase.feature.auth.data.source.remote.AuthApiService
import com.demo.projectbase.feature.auth.data.source.remote.AuthRemoteDataSource
import com.demo.projectbase.feature.auth.domain.repository.AuthRepository
import com.demo.projectbase.feature.auth.domain.usecase.LoginUseCase
import com.demo.projectbase.feature.auth.domain.usecase.ValidateEmailUseCase
import com.demo.projectbase.feature.auth.domain.usecase.ValidatePasswordUseCase
import com.demo.projectbase.feature.auth.presentation.login.LoginViewModel
import com.demo.projectbase.feature.auth.presentation.register.RegisterViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val authModule = module {
    // data
    single { AuthInterceptor(apiKey = NativeLib.getReadAccessToken()) }
    single { get<Retrofit>().create(AuthApiService::class.java) }
    single { AuthRemoteDataSource(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get<SecurePreferencesManager>()) }

    // domain
    factory { LoginUseCase(get()) }
    factory { ValidateEmailUseCase() }
    factory { ValidatePasswordUseCase() }

    // presentation
    viewModel<LoginViewModel> { LoginViewModel(get(), get(), get()) }
    viewModel<RegisterViewModel> { RegisterViewModel() }
}
