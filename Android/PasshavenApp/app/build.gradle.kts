

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("androidx.room")
}

android {
    namespace = "ru.ztrixdev.projects.passhavenapp"
    compileSdk = 36

    composeOptions {
        kotlinCompilerExtensionVersion = "1.0.1"
    }
    defaultConfig {
        applicationId = "ru.ztrixdev.projects.passhavenapp"
        minSdk = 30
        targetSdk = 36
        versionCode = 2
        versionName = "release 2.0"

        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
    }

    packaging {
        resources {
            excludes += listOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/license.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/notice.txt",
                "META-INF/ASL2.0",
                "META-INF/*.kotlin_module"
            )
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.add("-opt-in=kotlin.uuid.ExperimentalUuidApi")
    }
}

dependencies {
    implementation(libs.material)
    implementation(libs.androidx.ui.text)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui.graphics)
    val lazyVersion = "5.2.0"
    val jnaVersion = "5.17.0"
    val roomVersion = "2.8.3"

    implementation(libs.java.otp)
    implementation("commons-codec:commons-codec:1.16.0")
    implementation(libs.androidx.documentfile)
    implementation("com.goterl:lazysodium-android:$lazyVersion@aar")
    implementation("net.java.dev.jna:jna:$jnaVersion@aar")
    implementation("androidx.room:room-runtime:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    implementation(libs.androidx.navigation.compose)
    implementation(libs.core)
    implementation(libs.zxing.android.embedded)
    implementation(libs.androidx.compose.ui.text.google.fonts)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.symbol.processing.api)
    implementation(libs.filekit.dialogs.compose)
    implementation(libs.gson)
    implementation(kotlincrypto.hash.sha3)
    implementation(libs.material.icons.core)
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
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}