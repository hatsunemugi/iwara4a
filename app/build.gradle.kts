import org.gradle.kotlin.dsl.implementation

plugins {
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.gradle)
    alias(libs.plugins.ksp)
}
hilt {
    enableAggregatingTask = false
}
android {
    namespace = "com.ero.iwara"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ero.iwara"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}
dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.material)
    implementation(libs.dkplayer.java)
    implementation(libs.dkplayer.ui)
    implementation(libs.player.exo)
    implementation(libs.exoplayer)
    implementation(libs.eventbus)
    implementation(libs.material3)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.androidx.paging.compose)
    implementation(libs.hilt.android)
    implementation(libs.androidx.foundation)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.core)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.kotlinx.datetime) // 替换为最新版本
    implementation(libs.kotlinx.serialization.core) // 使用最新的稳定版本
    // Kotlinx Serialization JSON format (用于 Json 类)
    implementation(libs.kotlinx.serialization.json) // 使用最新的稳定版本
    // 状态栏颜色
    // implementation(libs.accompanist.systemuicontroller)
    // Coil
    implementation(libs.coil.gif)
    implementation(libs.coil.compose)
    // Retrofit
    implementation(libs.okhttp)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    // JSOUP
    implementation(libs.jsoup)
    // 约束布局
    implementation(libs.androidx.constraintlayout.compose)
    // 图标扩展
    implementation(libs.androidx.material.icons.extended)
    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

}
