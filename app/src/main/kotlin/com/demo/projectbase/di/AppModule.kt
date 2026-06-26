package com.demo.projectbase.di

import com.demo.projectbase.BuildConfig
import com.demo.projectbase.data.preferences.AppPreferencesManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    single(named("baseUrl")) { BuildConfig.BASE_URL }
    single { AppPreferencesManager(androidContext()) }
}
