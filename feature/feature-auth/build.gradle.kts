plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.demo.projectbase.feature.auth"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":core:core-ui"))
    implementation(project(":core:core-network"))

    implementation(libs.bundles.lifecycle)
    implementation(libs.coroutines.core)
    implementation(libs.koin.android)
    implementation(libs.material)
    implementation(libs.core.ktx)

    testImplementation(project(":core:core-testing"))
    testImplementation(libs.bundles.testing.unit)
    androidTestImplementation(project(":core:core-testing"))
}
