plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
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
        compose = true
    }
}

dependencies {
    implementation(project(":core:core-ui"))
    implementation(project(":core:core-network"))

    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.bundles.lifecycle)
    implementation(libs.coroutines.core)

    implementation(libs.koin.androidx.compose)

    debugImplementation(libs.compose.ui.tooling)

    // Testing
    testImplementation(project(":core:core-testing"))
    testImplementation(libs.bundles.testing.unit)
    androidTestImplementation(project(":core:core-testing"))
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.test.manifest)
}
