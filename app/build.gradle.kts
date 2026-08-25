import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

val localProperties = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}

// I injected the API key at build time via BuildCofig which is sourced from local.properties
// (which is gitignored)
val catApiKey: String = (localProperties.getProperty("CAT_API_KEY")
    ?: System.getenv("CAT_API_KEY")
    ?: "").also {
    if (it.isEmpty()) {
        logger.warn("CAT_API_KEY is not set. Add it to local.properties or as an env var.")
    }
}

android {
    namespace = "com.example.catsbreed"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.catsbreed"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "CAT_API_KEY", "\"$catApiKey\"")
        buildConfigField("String", "BASE_URL", "\"https://api.thecatapi.com/v1/\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation(libs.androidx.navigation)
    implementation(libs.androidx.compose.material.icons.extended)

    // DI - Koin
    implementation(libs.koin)
    implementation(libs.koinCompose)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofitConverter)
    implementation(libs.okhhtp)
    implementation(libs.okhhtpLogging)
    implementation(libs.kotlinSerialization)

    // Image loading
    implementation(libs.coil)

    // Coroutines
    implementation(libs.coroutines)

    // Unit Testing
    testImplementation(libs.coroutinesTest)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.truth)
    testImplementation(libs.coreTesting)

    // Room (Persistence)
    implementation(libs.roomRuntime)
    implementation(libs.roomKtx)
    ksp(libs.kspRoom)
}