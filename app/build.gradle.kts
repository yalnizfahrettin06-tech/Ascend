plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.yalnizfahrettin.azim"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.yalnizfahrettin.azim"
        minSdk = 26
        targetSdk = 35
        versionCode = 38
        versionName = "9.10.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        resourceConfigurations += listOf("tr", "en")
    }

    val releaseStore = providers.environmentVariable("ASCEND_KEYSTORE_PATH").orNull
    signingConfigs {
        if (releaseStore != null) create("release") {
            storeFile = file(releaseStore)
            storePassword = providers.environmentVariable("ASCEND_STORE_PASSWORD").orNull
            keyAlias = providers.environmentVariable("ASCEND_KEY_ALIAS").orNull
            keyPassword = providers.environmentVariable("ASCEND_KEY_PASSWORD").orNull
        }
    }

    buildTypes {
        release {
            // R8 varsayilan olarak ACIK. Zayif makinede kapatmak icin:
            //   ./gradlew :app:assembleRelease -PazimMinify=false
            val minify = (project.findProperty("azimMinify") as String? ?: "true").toBoolean()
            isMinifyEnabled = minify
            isShrinkResources = minify
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            if (releaseStore != null) signingConfig = signingConfigs.getByName("release")
        }
        debug {
            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true; buildConfig = true }
    // Both supported languages must remain available for the in-app language picker.
    bundle { language { enableSplit = false } }
    packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.activity:activity-compose:1.9.3")

    implementation(platform("androidx.compose:compose-bom:2024.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    implementation("androidx.navigation:navigation-compose:2.8.4")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("androidx.work:work-runtime-ktx:2.10.0")
    implementation("androidx.glance:glance-appwidget:1.1.1")

    debugImplementation("androidx.compose.ui:ui-tooling")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
