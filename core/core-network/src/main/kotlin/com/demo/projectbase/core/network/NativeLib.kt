package com.demo.projectbase.core.network

object NativeLib {
    init {
        System.loadLibrary("corenetwork")
    }

    external fun getApiKey(): String

    external fun getReadAccessToken(): String
}
