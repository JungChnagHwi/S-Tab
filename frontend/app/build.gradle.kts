import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

val localProperties = Properties()
rootProject.file("local.properties").inputStream().use {
    localProperties.load(it)
}
val kakaoAppKey = localProperties.getProperty("kakao_native_app_key") ?: "default_key"

android {
    namespace = "com.ssafy.stab"
    compileSdk = 34
    ndkVersion = "27.0.11718014"

    defaultConfig {
        applicationId = "com.ssafy.stab"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        buildConfigField("String", "KAKAO_APP_KEY", "\"$kakaoAppKey\"")
        resValue("string", "kakao_oauth_host", "kakao\"$kakaoAppKey\"")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}


dependencies {

    implementation(libs.gson)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.socket.io.client)
<<<<<<< PATCH SET (536648 chore: mediasoup client application 관련 build 설정 제거)
=======
    implementation(libs.mediasoup.client)
    implementation(libs.coil.compose)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
>>>>>>> BASE      (1ad9d4 fix: gpt 서버 유저별 대화내역 관리)
    val nav_version = "2.7.7"
    implementation("androidx.navigation:navigation-compose:$nav_version")
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

    implementation("com.kakao.sdk:v2-user:2.20.1") // 카카오 로그인 API 모듈
    implementation(files("libs/mediasoup-client-debug.aar")) // aar 파일

}