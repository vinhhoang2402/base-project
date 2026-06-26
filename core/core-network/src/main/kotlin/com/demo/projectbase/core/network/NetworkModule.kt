package com.demo.projectbase.core.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val networkModule = module {
    single { SecurePreferencesManager(androidContext()) }

    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(ErrorInterceptor())
            .addInterceptor(get<AuthInterceptor>())
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                }
            }
            .build()
    }

    single {
        val json: Json = get()
        Retrofit.Builder()
            .baseUrl(get<String>(named("baseUrl")))
            .client(get())
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
}
