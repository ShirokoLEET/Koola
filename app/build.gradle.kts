plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "cn.peyriat.koola"
    compileSdk = 35

    defaultConfig {

        applicationId = "cn.peyriat.koola"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        ndk {
            abiFilters += listOf("armeabi-v7a","arm64-v8a")
        }

        externalNativeBuild {
            cmake {
                cppFlags += ""
            }
        }


    }



    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            //proguardFiles("proguard-rules.pro")
            signingConfig = signingConfigs["debug"]
        }
    }
    packaging {
        resources {
            merges += "META-INF/xposed/*"
            excludes += "**"
        }
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
        prefab = true
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
    packagingOptions{
        pickFirsts += listOf(
            "**/libshadowhook.so",
            "**/libshadowhook_nothing.so"
        )
    }
}

dependencies {
    implementation(libs.androidx.cardview)
    implementation(libs.shadowhook)
    implementation(libs.androidx.room.common)
    compileOnly(libs.xposed.api)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}